import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.io.*;

public class Visit {

    private String parkingArea = "";
    Scanner scan = new Scanner(System.in);
    String memberId;
    private String reservedSpot; // isReservedUser()에서 씀.
    private String carNum; // for license plate number
    private String currentTime;
    String clearDateTime;
    private boolean[][] parkA = new boolean[4][4];
    private boolean[][] parkB = new boolean[4][4];

    String pathname;
    int input0, input1, input2, input3, input4;

    public Visit(String currentTime, String memberId) {
        this.currentTime = currentTime;
        this.memberId = memberId;
    }
    MemberManagement memberManagement ;

    public void menu()
    {
        if(!isOpen()) {
            System.out.println("주차장 운영 시간이 아닙니다.");
            return;
        }

        int menu =0;
        boolean flag = true;

        makePathname();

        while(flag)
        {
            System.out.println("1)입차 2)출차");
            System.out.print(">>>");
            String menuS = scan.nextLine();
            String[] split = menuS.split("");

            if(split.length != 1)
            {
                System.out.println("잘못된 형식입니다. 다시 입력해주세요.");
                continue;
            }

            //split[0]: 선택한 메뉴
            if(split[0].equals("1")) {
                //해당 회원의 차량이 주차 중인지 user.txt파일 확인
                //주차 중이면 다시 입차 출차 메뉴 선택으로 돌아감
                if(isAlreadyParked())
                    continue;
                menu = Integer.parseInt(split[0]);
                break;
            }
            else if (split[0].equals("2")) {
                menu = Integer.parseInt(split[0]);
                break;
            }

            System.out.println("잘못된 형식입니다. 다시 입력해주세요.");
        }

        switch (menu){
            case 1:
                //입차
                carIn();
                break;
            case 2:
                //출차
                carOut();
                break;
        }
    }


    private boolean isOpen() {
        System.out.println();
        return true;
    }

    //carIn && carOut 공통 구역 =========================
    private boolean inputCarNum(){
        while(true) { //올바른 형식을 입력할 때까지 while문 무한반복
            System.out.println("차량 번호를 입력하세요 ex)123-가-1234");
            System.out.print(">>>");

            carNum = scan.nextLine();
            int hyphenNum = carNum.length() - carNum.replace("-","").length();
            String[] carNumPiece = carNum.split("-");
            if(!(carNumPiece.length == 3 && hyphenNum == 2)) {
                System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                continue;
            }

            String[] pattern = {"\\d{3}", "[가-힣]{1}", "\\d{4}"};
            boolean isLicensePlateSuitableToForm = true; //입력한 번호판 형식이 틀리면 false로 바뀜
            for(int i = 0; i < 3; i++) {
                if (!Pattern.matches(pattern[i], carNumPiece[i])) {
                    //입력 형식이 틀린 경우
                    isLicensePlateSuitableToForm = false;
                    break;
                }
            }
            if(!isLicensePlateSuitableToForm) {
                //입력한 번호판 형식이 틀림
                System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                continue;
            } else {
                System.out.println("입력이 완료되었습니다.");
                break;
            }
        }
        return true;
    }

    private void makePathname() {
        //전역변수로 선언된 pathname 생성. pathname은 현재 날짜와 시간 정보를 저장
        String[] input = currentTime.split("-|/|:");
        for(int i=0; i<input.length; i++) {
            input[i] = input[i].replaceFirst("^0+(?!$)", "");
        }

        input0 = (int)Double.parseDouble(input[0]);
        input1 = (int)Double.parseDouble(input[1]);
        input2 = (int)Double.parseDouble(input[2]);
        input3 = (int)Double.parseDouble(input[3]);
        input4 = (int)Double.parseDouble(input[4]);

        clearDateTime = input0+"-"+input1+"-"+input2+"/"+input3+":"+input4;
        pathname = input0+"-"+input1+"-"+input2;
    }

    private boolean isCarExist() {
        //String[] split = currentTime.split("/");
        //currentTime: 생상자로 받아온 현재 날짜와 시각
        // 입력 예시: 2022-9-28/14:00

        StringBuffer sb = new StringBuffer();
        FileReader readFile;
        String getLine;

        try {
            readFile = new FileReader(pathname + "/visited.txt");
            BufferedReader br = new BufferedReader(readFile);

            while((getLine = br.readLine()) != null) {
                //visited.txt  첫줄부터 읽기 시작
                String[] txtSplit = getLine.split(" "); //공백으로 구분
                if(txtSplit[1].contains(carNum)) {
                    if(isSametoCurrentUserId()){
                        System.out.println(memberId + " 사용자의 " + carNum + "차량은 현재 주차되어있는 차량입니다.");
                        br.close();
                        return false;
                    } else{
                        System.out.println(carNum + "차량은 현재 주차되어있는 차량입니다.");
                        System.out.println("사용자ID와 차량번호가 일치하지 않습니다.");
                        br.close();
                        return false;
                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return true; //차량이 존재하지 않으면 true 반환
    }

    private boolean isSametoCurrentUserId(){
        File UserTxt = new File("User.txt");
        FileReader readUserFile;
        String getUserLine;
        try {
            readUserFile = new FileReader(UserTxt);
            BufferedReader br = new BufferedReader(readUserFile);

            while ((getUserLine = br.readLine()) != null) {
                if ((getUserLine.contains(carNum)) && (Objects.equals(memberId, getUserLine.split(" ")[0]))) {
                    br.close();
                    return true;
                }
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void carIn(){
        //입차

        //1. 차량번호 입력 및 그 차량이 주차장에 존재하는지 확인
        boolean flag = false;
        while(!flag)
        {
            flag = inputCarNum() && isCarExist();
            if(!flag) {
                System.out.println("잘못된 입력입니다. 다시 입력해주세요");
            }
        }
        System.out.println("차량 번호 확인이 완료되었습니다.");

        //2. 해당 회원 정보에 등록된 차량인지 확인
        memberManagement = new MemberManagement(memberId);
        if(memberManagement.addNewCarToMember(carNum)) {
            //신규 차량 등록
            System.out.println("신규 차량을 등록합니다.");
        }

        //3. 예약 고객인지 확인
        if(isReservedUser()) {
            // when user have reserved
            if(isReservedSeatOccupied()) {
                if(noEmptySeats()) {
                    forceExitVisiter();
                    parkingArea = reservedSpot;
                    deleteReservationAfterReservationCheck();
                    entryCompleted();
                } else {
                    // 먼저 입차한사람 정보를 임시로 저장한 뒤 visited.txt에서 삭제하고
                    // 예약자를 예약자리에 넣은뒤, if noEmptySeats면 강제출차, 아니면 빈자리 찾아서 입차시켜줌
                    String[] occupyingVisiterInfo = occupyingVisiter().split(" ");
                    forceExitVisiter();
                    parkingArea = reservedSpot;
                    entryCompleted();
                    deleteReservationAfterReservationCheck();

                    if(noEmptySeats()) {
                        System.out.println("자리를 점유하고 있는 고객은 강제출차되었습니다.");
                    } else {
                        boolean isFindingEmptySpotNotOver = true;
                        while(isFindingEmptySpotNotOver){
                            for(int j=0; j<4; j++) {
                                for(int i=0; i<4; i++) {
                                    if(!parkA[i][j]){
                                        // 그 자리에 입차시킴.
                                        parkingArea = "A-"+i+"-"+j;
                                        carNum = occupyingVisiterInfo[1];
                                        entryCompleted();
                                        isFindingEmptySpotNotOver = false;
                                        break;
                                    } else if(!parkB[i][j]) {
                                        // 그 자리에 입차시킴.
                                        parkingArea = "B-"+i+"-"+j;
                                        carNum = occupyingVisiterInfo[1];
                                        entryCompleted();
                                        isFindingEmptySpotNotOver = false;
                                        break;
                                    }
                                }
                                if(isFindingEmptySpotNotOver == false) {
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
            } else {
                parkingArea = reservedSpot;
                deleteReservationAfterReservationCheck();
                entryCompleted();
            }
        } else {
            //미예약 고객
            printParkingStatus();
            enterParkingSeat();
            entryCompleted();//이게 txt 넣는 곳
        }
    }


    private boolean isAlreadyParked() {
        //파일 읽기
        FileReader readFile;
        String getLine;

        String[] memberCarNumList = {};
        try {
            //1. user.txt에서 사용자의 차량 저장
            readFile = new FileReader("User.txt");
            BufferedReader br = new BufferedReader(readFile);

            while ((getLine = br.readLine()) != null) {
                if (getLine.contains(memberId)) {
                    memberCarNumList = getLine.split(" ");
                    break;
                }
            }

            //2. 오늘 날짜에 해당하는 텍스트파일에서 해당 차량이 있는지 확인
            readFile = new FileReader(pathname + "/visited.txt");
            br.close();
            br = new BufferedReader(readFile);

            while ((getLine = br.readLine()) != null) {
                for (int i = 1; i < memberCarNumList.length; i++) {
                    if (getLine.contains(memberCarNumList[i])) {
                        System.out.println("회원님의 차량이 이미 주차 중입니다.");
                        br.close();
                        return true;
                    }
                }
            }
            br.close();
            System.out.println("주차중인 회원님의 차량이 없습니다. 다음 단계로 이동합니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String occupyingVisiter() {
        String occupyingVisiter = "";

        String[] split = currentTime.split("/");
        System.out.println(split[0]);

        StringBuffer sb = new StringBuffer();
        FileReader readFile;
        String getLine;

        try {
            readFile = new FileReader(split[0] + "/visited.txt");

            BufferedReader br = new BufferedReader(readFile);
            while((getLine = br.readLine()) != null) {
                //주차구역 차량번호 현재시간이 저장된 줄부터 읽기 시작
                String[] txtSplit = getLine.split(" "); //공백으로 구분
                if(txtSplit[0].contains(reservedSpot)) {
                    occupyingVisiter = getLine; //차량이 존재하면 true 반환
                }
            }
            br.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return occupyingVisiter;
    }

    private boolean isReservedUser() {
        System.out.println("예약을 했는지 확인하는중입니다...");

        boolean isCarReserved = false;

        String[] splitCurrentTimeforReserveCheck = currentTime.split("/");
        //currentTime: 생상자로 받아온 현재 날짜와 시각

        StringBuffer sb = new StringBuffer();
        FileReader readFile;
        String getLine;

        String[] input = currentTime.split("-|/|:");
        for (int i = 0; i < input.length; i++) {
            input[i] = input[i].replaceFirst("^0+(?!$)", "");
        }

        input0 = (int) Double.parseDouble(input[0]);
        input1 = (int) Double.parseDouble(input[1]);
        input2 = (int) Double.parseDouble(input[2]);
        input3 = (int) Double.parseDouble(input[3]);
        input4 = (int) Double.parseDouble(input[4]);

        clearDateTime = input0 + "-" + input1 + "-" + input2 + "/" + input3 + ":" + input4;
        pathname = input0 + "-" + input1 + "-" + input2;
        try {
            readFile = new FileReader(pathname + "/booked.txt");

            BufferedReader brforReserveCheck = new BufferedReader(readFile);

            while ((getLine = brforReserveCheck.readLine()) != null) {
                //주차구역 차량번호 현재시간이 저장된 줄부터 읽기 시작
                String[] txtSplit = getLine.split(" "); //공백으로 구분
                if (txtSplit[1].contains(carNum)) {
                    reservedSpot = txtSplit[0];
                    System.out.println("고객님의 예약된 자리에 주차될 차량: " + carNum);
                    isCarReserved = true; // returns true if the car has reserved
                }
            }
            brforReserveCheck.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("isCarReserved: "+isCarReserved);
        System.out.println("Complete");
        return isCarReserved;
    }

    private boolean isReservedSeatOccupied() {
        System.out.println("해당 자리가 이미 예약되었는지 확인하는 중입니다...");
        boolean isReservedSeatOccupied = false;

        String[] split = currentTime.split("/");

        StringBuffer sb = new StringBuffer();
        FileReader readFile;
        String getLine;


        try {
            // 사용자가 날짜 입력에 0 포함되어있으면 에러남 - currentTime 변수 0 없애서 저장해주셈.
            readFile = new FileReader(split[0] + "/visited.txt");

            BufferedReader br = new BufferedReader(readFile);
            while((getLine = br.readLine()) != null) {
                //주차구역 차량번호 현재시간이 저장된 줄부터 읽기 시작
                String[] txtSplit = getLine.split(" "); //공백으로 구분
                if(txtSplit[0].contains(reservedSpot)) {
                    isReservedSeatOccupied = true; //차량이 존재하면 true 반환
                }
            }
            br.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return isReservedSeatOccupied;
    }

    private void forceExitVisiter() {
        // 예약자리에 입차 차량이 있을때 출차처리함.

        System.out.println("자리를 점유하고 있는 고객을 강제출차하는 중입니다...");
        String[] split = clearDateTime.split("/");
        String getLine;
        try{
            File currentTime = new File(split[0] + "/visited.txt");

            File tmpFile = new File(split[0]+ "/$$$$$$$$.txt");
            FileOutputStream streamOutFortmpFile = new FileOutputStream(tmpFile);
            PrintWriter writerOutFortmpFile = new PrintWriter(streamOutFortmpFile);
            FileInputStream currentFile = new FileInputStream(split[0]+ "/visited.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(currentFile));

            while((getLine = br.readLine()) != null){
                if(getLine.contains(reservedSpot)){
                    continue;
                } else {
                    writerOutFortmpFile.println(getLine);
                }
            }
            writerOutFortmpFile.flush();
            writerOutFortmpFile.close();
            streamOutFortmpFile.close();
            br.close();
            currentTime.delete();
            //임시파일을 원래 파일명으로 변경
            tmpFile.renameTo(currentTime);

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private boolean noEmptySeats() {
        // 주차장 loop 돌리기
        System.out.println("비어있는 자리가 있는지 확인하는 중입니다...");
        boolean isParkinglotFull = true;

        String[] input = currentTime.split("-|/|:");
        for(int i=0; i<input.length; i++) {
            input[i] = input[i].replaceFirst("^0+(?!$)", "");
        }

        input0 = (int)Double.parseDouble(input[0]);
        input1 = (int)Double.parseDouble(input[1]);
        input2 = (int)Double.parseDouble(input[2]);
        input3 = (int)Double.parseDouble(input[3]);
        input4 = (int)Double.parseDouble(input[4]);

        clearDateTime = input0+"-"+input1+"-"+input2+"/"+input3+":"+input4;
        pathname = input0+"-"+input1+"-"+input2;
        int countA = 0;
        int countB =0;
        try{
            FileInputStream currentFile = new FileInputStream(pathname + "/visited.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(currentFile));
            String getLine = "";
            int k=0;
            while((getLine = in.readLine()) != null)
            {
                String[] fullString = getLine.split(" ");
                String[] split = fullString[0].split("-");
                int n1 = Integer.parseInt(split[1]);
                int n2 = Integer.parseInt(split[2]);
                if(split[0].equals("A"))
                {
                    parkA[n1][n2] = true;
                    countA++;
                }
                else if(split[0].equals("B"))
                {
                    parkB[n1][n2] = true;
                    countB++;
                }
                k++;
            }
            in.close();
        } catch (Exception e)
        {
            e.getStackTrace();
        }

        for (int i = 0; i < parkA.length; i++) {
            for (int j = 0; j < parkA[0].length; j++) {
                if(!parkA[i][j])
                    isParkinglotFull = false;
            }
        }
        for (int i = 0; i < parkB.length; i++) {
            for (int j = 0; j < parkB[0].length; j++) {
                if(parkB[i][j])
                    isParkinglotFull = false;
            }
        }

        return isParkinglotFull;
    }

    private void deleteReservationAfterReservationCheck() {
        // 원래 미예약 입차 고객이라면 입력을 받는 parkingArea를 예약내역에 있는 주차 자리로 갱신해줌.

        System.out.println("고객님이 방문하셨으니 예약내용을 삭제하는 중입니다...");
        String[] split = clearDateTime.split("/");
        String getLine;
        try{
            File currentTime = new File(split[0] + "/booked.txt");

            File tmpFile = new File(split[0]+ "/tempBook.txt");
            FileOutputStream streamOutFortmpFile = new FileOutputStream(tmpFile);
            PrintWriter writerOutFortmpFile = new PrintWriter(streamOutFortmpFile);
            FileInputStream currentFile = new FileInputStream(split[0]+ "/Booked.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(currentFile));

            while((getLine = br.readLine()) != null){
                if(getLine.contains(reservedSpot)){
                    continue;
                } else {
                    writerOutFortmpFile.println(getLine);
                }
            }

            writerOutFortmpFile.flush();
            writerOutFortmpFile.close();
            streamOutFortmpFile.close();
            br.close();
            currentTime.delete();
            //임시파일을 원래 파일명으로 변경
            tmpFile.renameTo(currentTime);
            System.out.println("Complete");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void enterParkingSeat() {
        //올바르게 입력 할때까지 무한루프
        boolean A = true;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                A = A && parkA[i][j];
            }
        }
        boolean B = true;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                B = B && parkB[i][j];
            }
        }
        if(A && B) {
            //parkA와 parkB에 모두 더이상 주차할 자리가 없는 경우
            System.out.println("자리가 모두 꽉차 예약할 수 없습니다.");
            System.exit(0);
        }

        boolean flag = true;
        while(flag) {
            System.out.println("주차할 자리를 선택하세요 (ex>>>A-1-3)");
            System.out.print(">>>");
            Scanner scan = new Scanner(System.in);
            String area = scan.next();
            String[] split = area.split("-");

            if(area.charAt(area.length()-1) == '-')
            {
                System.out.println("잘못된 형식입니다. 다시 입력해주세요.");
                continue;
            }
            if(split.length != 3)
            {
                System.out.println("잘못된 형식입니다. 다시 입력해주세요.");
                continue;
            }
            if(split[0].length() < 1 || split[1].length() < 1 || split[2].length() < 1){
                System.out.println("잘못된 형식입니다. 다시 입력해주세요.");
                continue;
            }
            char[] chars = new char[3];
            boolean tooLong = false;
            for (int i = 0; i < 3; i++) {
                if(split[i].length()>1) // AA, 1.2 같이 각 구분자 사이 길이 2 이상이면 짜름
                {
                    System.out.println("잘못된 형식입니다. 다시 입력해주세요.");
                    tooLong = true;
                    break;
                }
                chars[i] = split[i].charAt(0);
            }
            if(tooLong)
            {
                continue;
            }
            if(chars[0] != 'A' && chars[0] != 'B') //첫번째 문자
            {
                System.out.println("잘못된 형식입니다. 다시 입력해주세요.");
                continue;
            }
            if(chars[1] <48 || chars[1] >=52 || chars[2] <48 || chars[2] >=52) // 두 세번째 숫자
            {
                System.out.println("잘못된 형식입니다. 다시 입력해주세요.");
                continue;
            }

            if(chars[0] == 'A' && parkA[chars[1] - '0'][chars[2] -'0']) //parkA에서 차가 이미 들어가 있는지 확인
            {
                System.out.println("입차 불가능한 자리입니다.");
                continue;
            }
            else if(chars[0] == 'B' && parkB[chars[1] - '0'][chars[2] -'0'])  //parkA에서 차가 이미 들어가 있는지 확인
            {
                System.out.println("입차 불가능한 자리입니다.");
                continue;
            }
            parkingArea = chars[0] + "-" + chars[1] + "-" + chars[2];
            flag = false;
            System.out.println("입차가 완료되었습니다.");
        }
    }

    private void printParkingStatus() {
        String[] input = currentTime.split("-|/|:");
        for(int i=0; i<input.length; i++) {
            input[i] = input[i].replaceFirst("^0+(?!$)", "");
        }

        input0 = (int)Double.parseDouble(input[0]);
        input1 = (int)Double.parseDouble(input[1]);
        input2 = (int)Double.parseDouble(input[2]);
        input3 = (int)Double.parseDouble(input[3]);
        input4 = (int)Double.parseDouble(input[4]);

        clearDateTime = input0+"-"+input1+"-"+input2+"/"+input3+":"+input4;
        pathname = input0+"-"+input1+"-"+input2;
        int countA = 0;
        int countB =0;
        try{
            FileInputStream currentFile = new FileInputStream(pathname + "/visited.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(currentFile));
            String getLine = "";
            int k=0;
            while((getLine = in.readLine()) != null)
            {
                String[] fullString = getLine.split(" ");
                String[] split = fullString[0].split("-");
                int n1 = Integer.parseInt(split[1]);
                int n2 = Integer.parseInt(split[2]);
                if(split[0].equals("A"))
                {
                    parkA[n1][n2] = true;
                    countA++;
                }
                else if(split[0].equals("B"))
                {
                    parkB[n1][n2] = true;
                    countB++;
                }
                k++;
            }
            in.close();
        } catch (Exception e)
        {
            e.getStackTrace();
        }
        System.out.println("A구역 : " + countA + "/16");
        for (int i = 0; i < parkA.length; i++) {
            for (int j = 0; j < parkA[0].length; j++) {
                if(parkA[i][j])
                    System.out.print("■ ");
                else
                    System.out.print("□ ");
            }
            System.out.println();
        }
        System.out.println("B구역 : " + countB + "/16");
        for (int i = 0; i < parkB.length; i++) {
            for (int j = 0; j < parkB[0].length; j++) {
                if(parkB[i][j])
                    System.out.print("■ ");
                else
                    System.out.print("□ ");
            }
            System.out.println();
        }
    }

    private void entryCompleted() {

        String[] split = clearDateTime.split("/");
        String date = split[0];
        int menu = 1;
        File dir = new File(pathname);
        if(dir.exists())
        {
            try{
                FileOutputStream fVisited= new FileOutputStream(pathname + "/visited.txt",true);
                String fullString = parkingArea + " " + carNum + " " + clearDateTime +"\n";
                fVisited.write(fullString.getBytes());
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    // carIn 구역 끝==========================================

    // carOut 구역 시작==========================================
    private void carOut(){
        boolean flag = false;
        String[] input = currentTime.split("-|/|:");
        for(int i=0; i<input.length; i++) {
            input[i] = input[i].replaceFirst("^0+(?!$)", "");
        }

        input0 = (int)Double.parseDouble(input[0]);
        input1 = (int)Double.parseDouble(input[1]);
        input2 = (int)Double.parseDouble(input[2]);
        input3 = (int)Double.parseDouble(input[3]);
        input4 = (int)Double.parseDouble(input[4]);

        clearDateTime = input0+"-"+input1+"-"+input2+"/"+input3+":"+input4;
        pathname = input0+"-"+input1+"-"+input2;

        try{
            FileInputStream currentFile = new FileInputStream(pathname + "/visited.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(currentFile));
            if(in.readLine() == null)
            {
                System.out.println("출차할 차량이 없습니다.");
                System.exit(0);
            }
            in.close();
        }catch (Exception e)
        {
            e.getStackTrace();
        }
        while(!flag)
        {
            flag = inputCarNum() && !isCarExist(); //형식이 올바르고 현재 주차중인 차량이라면
            if(!flag)
                System.out.println("잘못된 입력입니다. 다시 입력해주세요");
            else
            {
                exitCompleted();
                System.out.println("안녕히 가십시오");
            }

        }
    }

    private void exitCompleted() {
        //visited 텍스트 파일에서 해당 차량 번호 찾아서 없애야함

        String[] split = clearDateTime.split("/");

        String getLine;
        try{
            File currentTime = new File(split[0] + "/visited.txt");
            File tmpFile = new File(split[0]+ "/$$$$$$$$.txt");
            FileOutputStream fout1 = new FileOutputStream(tmpFile);
            PrintWriter out = new PrintWriter(fout1);
            FileInputStream currentFile = new FileInputStream(split[0]+ "/visited.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(currentFile));

            while((getLine = br.readLine()) != null){
                String line;

                if(getLine.contains(carNum))
                    continue;
                out.println(getLine);

            }
            out.flush();
            out.close();
            fout1.close();
            br.close();
            currentTime.delete();
            //임시파일을 원래 파일명으로 변경
            tmpFile.renameTo(currentTime);

        }catch(IOException e){
            e.printStackTrace();
        }
    }


}






