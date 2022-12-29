import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.lang.Integer;
public class Reservation {

    private String currentTime;
    private String currentTimeWithoutZero;
    private String carNum;
    private boolean[][] parkA = new boolean[4][4];
    private boolean[][] parkB = new boolean[4][4];
    //printParkingStatus()에서, parkA와 parkB에 txt파일로부터 주차 정보를 가져와 저장함
    //parkA[i][j] = true면 주차된 자리, false이면 주차 가능한 자리
    private String memberId;
    private String reservationArea = ""; //주차한 위치
    private String pathName;
    private String clearReservationTime;
    private String resTime;
    int currentYear;
    int currentMonth;
    int currentDate;
    int currentHour;
    int currentMinute;
    int reservationYear;
    int reservationMonth;
    int reservationDate;
    int reservationHour;
    int reservationMinute;
    String reservationTime;

    public Reservation(String currentTime,String memberId) {
        this.currentTime = currentTime;
        this.memberId = memberId;
    }
    MemberManagement memberManagement ;

    public void reservation() throws ParseException
    {
        //blacklist에 있는 회원인지 확인
        if(isOnBlacklist()) {
            System.out.println("예약 서비스가 제한된 회원입니다.");
            System.exit(0);
        }

        boolean flag = false;
        while(!flag)
        {
            flag = inputReservationTime();
            if(!flag)
                System.out.println("잘못된 입력입니다. 다시 입력해주세요");
        }

        boolean isNotOk = true;
        while(isNotOk)
        {
            printParkingStatus();//예약 가능한 자리면 true 불가능한 자리면 false 로 2차원 배열 parkA,park B 에 저장해주세용
            enterParkingSeat(); //주차를 원하는 자리 입력
            if(inputCarNum()) { //올바른 형식으로 입력했는지 확인

                isNotOk = isAlreadyReserved() || isAlreadyParked();
                if(isNotOk) {
                    System.out.println("이미 예약되어있습니다. 다시 입력하세요");
                }
                else {
                    isNotOk = false;
                    reservationCompleted();
                    System.out.println("예약이 완료되었습니다.");
                }
            }
        }
    }

    private boolean isOnBlacklist() {
        File blacklistTxt = new File("blacklist.txt");

        StringBuffer sb = new StringBuffer();
        FileReader readFile;
        String getLine;
        try {
            if (!blacklistTxt.exists())
                blacklistTxt.createNewFile();
        } catch (Exception e) {
            e.getStackTrace();
        }
        try {
            readFile = new FileReader(blacklistTxt);
            BufferedReader br = new BufferedReader(readFile);

            while ((getLine = br.readLine()) != null) {
                if (getLine.contains(memberId)) {
                    return true;
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void enterParkingSeat() { //올바르게 입력 할때까지 무한루프
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
        if(A && B) //parkA와 parkB에 모두 더이상 주차할 자리가 없는 경우
        {
            System.out.println("자리가 모두 꽉차 예약할 수 없습니다.");
            System.exit(0);
        }

        boolean flag = true;
        while(flag)
        {
            System.out.print("주차할 자리를 선택하세요 ex)A-0-0 :  ");
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
            char[]  chars = new char[3];
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
                System.out.println("예약 불가능한 자리입니다.");
                continue;
            }
            else if(chars[0] == 'B' && parkB[chars[1] - '0'][chars[2] -'0'])  //parkA에서 차가 이미 들어가 있는지 확인
            {
                System.out.println("예약 불가능한 자리입니다.");
                continue;
            }
            reservationArea = chars[0] + "-" + chars[1] + "-" + chars[2];
            flag = false;
            System.out.println("예약 구역 선택이 완료되었습니다.");
        }
    }

    private boolean inputCarNum(){//visit에서 재활용 //올바르게 입력 할때까지 무한루프
        Scanner scan = new Scanner(System.in);
        while(true) { //올바른 형식을 입력할 때까지 while문 무한반복
            System.out.println("차량 번호를 입력하세요 ex)123-가-1234");
            System.out.print(">>>");
            carNum = scan.next();
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

    private void reservationCompleted() {//txt에 저장

        String[] split = clearReservationTime.split("/");
        int menu = 1;
        File dir = new File(split[0]);

        //사용자가 새로운 차량을 등록하는 경우 사용자 정보(전화번호, 차량번호)를 User.txt에 저장
        memberManagement = new MemberManagement(memberId);
        if(memberManagement.addNewCarToMember(carNum)) {
            //신규 차량 등록
            System.out.println("신규 차량을 등록합니다.");
        }

        //예약정보(예약위치, 차량번호, 예약시간)를 booked.txt에 저장
        if(dir.exists())
        {
            try{
                FileOutputStream fVisited= new FileOutputStream(split[0] + "/booked.txt",true);
                String fullString = reservationArea + " " + carNum + " " + clearReservationTime +"\n";
                fVisited.write(fullString.getBytes());
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }



    }

    private boolean isAlreadyParked(){
        String[] split = clearReservationTime.split("/");
        //currentTime: 생상자로 받아온 현재 날짜와 시각
        //(입력 예시: 2022-9-28/14:00)

        StringBuffer sb = new StringBuffer();
        FileReader readFile;
        String getLine;
        //File reserveFile = new File(split[0]);
        try {

            readFile = new FileReader(split[0] + "/visited.txt");

            BufferedReader br = new BufferedReader(readFile);
            while((getLine = br.readLine()) != null) {
                //주차구역 차량번호 현재시간이 저장된 줄부터 읽기 시작f
                String[] txtSplit = getLine.split(" "); //공백으로 구분
                if(txtSplit[1].contains(carNum)) {
                    System.out.println(carNum + "차량은 이미 주차되어있는 차량입니다.");
                    return true; //차량이 존재하면 true 반환
                }
            }
            br.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return false; //차량이 존재하지 않으면 false 반환
    }

    private boolean isAlreadyReserved() {
        String[] split = clearReservationTime.split("/");
        //currentTime: 생상자로 받아온 현재 날짜와 시각
        //(입력 예시: 2022-9-28/14:00)

        StringBuffer sb = new StringBuffer();
        FileReader readFile;
        String getLine;
        //File reserveFile = new File(split[0]);
        try {

            readFile = new FileReader(split[0] + "/booked.txt");

            BufferedReader br = new BufferedReader(readFile);
            while((getLine = br.readLine()) != null) {
                //주차구역 차량번호 현재시간이 저장된 줄부터 읽기 시작
                String[] txtSplit = getLine.split(" "); //공백으로 구분
                if(txtSplit[1].contains(carNum)) {
                    System.out.println(carNum + "차량은 이미 예약되어있는 차량입니다.");
                    return true; //차량이 존재하면 true 반환
                }
            }
            br.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return false; //차량이 존재하지 않으면 false 반환
    }

    private void printParkingStatus() throws ParseException {
        // 예약 날짜에 대한 현황만 출력하면 됨
        String[] input = clearReservationTime.split("-|/|:");
        for(int i=0; i<input.length; i++) {
            input[i] = input[i].replaceFirst("^0+(?!$)", "");
        }
        int countA =0;
        int countB =0;
        currentYear = (int)Double.parseDouble(input[0]);
        currentMonth = (int)Double.parseDouble(input[1]);
        currentDate = (int)Double.parseDouble(input[2]);
        currentHour = (int)Double.parseDouble(input[3]);
        currentMinute = (int)Double.parseDouble(input[4]);

        currentTimeWithoutZero = currentYear +"-"+ currentMonth +"-"+ currentDate +"/"+ currentHour +":"+ currentMinute;
        pathName = currentYear +"-"+ currentMonth +"-"+ currentDate;

        String[] sp = currentTimeWithoutZero.split("/");
        //(입력 예시: 2022-9-28/14:00)

        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-mm-dd/HH:mm");
        Date resDate = timeFormat.parse(currentTimeWithoutZero);
        Calendar resCal = Calendar.getInstance();
        resCal.setTime(resDate);
        int resHour = resCal.get(Calendar.HOUR_OF_DAY);

        // 예약은 최대 4시간 유효하므로 예약 시간 -4시간 했는데 이미 예약자가 있으면 안됨.
        StringBuffer sb = new StringBuffer();
        FileReader readBookedFile;
        FileReader readVisitedFile;
        String getLine;

        try {
            readBookedFile = new FileReader(pathName + "/booked.txt");
            readVisitedFile = new FileReader(pathName + "/visited.txt");

            // 방문자 처리
            // 현재 날짜와 예약 날짜가 다르면 처리 안 해줘도 되고, 같으면 처리(Visit.java에서 활용 가능)
            // inputReservationTime에서 이미 3일 이후나 이전 날짜 처리 했으니 연도는 체크 안 해도 됨. 월일이 같은데 연도가 다를 수가 없음
            String[] currentDate = currentTimeWithoutZero.split("/")[0].split("-"); // 2022 09 22
            String[] rsvDate = clearReservationTime.split("/")[0].split("-"); // 2022 09 22
            if(rsvDate[1].equals(currentDate[1]) && rsvDate[2].equals(currentDate[2])){
                BufferedReader brr = new BufferedReader(readVisitedFile);
                while((getLine = brr.readLine()) != null&&!(getLine).equals(""))
                {
                    String[] seat = getLine.split(" ")[0].split("-");
                    if(seat[0].equals("A")){
                        parkA[Integer.parseInt(seat[1])][Integer.parseInt(seat[2])] = true;
                        countA++;
                    }
                    else{
                        parkB[Integer.parseInt(seat[1])][Integer.parseInt(seat[2])] = true;
                        countB++;
                    }

                }
            }

            // 예약자 처리
            BufferedReader br = new BufferedReader(readBookedFile);
            while((getLine = br.readLine()) != null) {
                Date bookedDate = timeFormat.parse(currentTimeWithoutZero);
                Calendar bookedCal = Calendar.getInstance();
                bookedCal.setTime(bookedDate);
                int bookedHour = bookedCal.get(Calendar.HOUR_OF_DAY);

                String[] txtSplit = getLine.split(" "); //공백으로 구분
                String[] splittedArea = txtSplit[0].split("-");
                if(splittedArea[0].equals("A")) { // A구역
                    if(bookedHour <= resHour && resHour <= bookedHour+4){
                        parkA[Integer.parseInt(splittedArea[1])][Integer.parseInt(splittedArea[2])] = true;
                        countA++;
                    }
                    else if(resHour <= bookedHour && bookedHour <= resHour+4) {
                        parkA[Integer.parseInt(splittedArea[1])][Integer.parseInt(splittedArea[2])] = true;
                        countA++;
                    }
                }
                else{ // B구역
                    if(bookedHour <= resHour && resHour <= bookedHour+4){
                        parkB[Integer.parseInt(splittedArea[1])][Integer.parseInt(splittedArea[2])] = true;
                        countB++;
                    }
                    else if(resHour <= bookedHour && bookedHour <= resHour+4){
                        parkB[Integer.parseInt(splittedArea[1])][Integer.parseInt(splittedArea[2])] = true;
                        countB++;
                    }
                }
            }
            br.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
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
        // 예약 시간 +4시간 이내 영업 종료라면 공지하는 코드 추후 작성
    }

    private boolean inputReservationTime() throws ParseException {

        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.println("예약 날짜 및 시간을 입력하세요. 시간은 30분 단위입니다. (입력 예시: 2022-09-29/12:00)");
            System.out.print("(종료하려면 q를 입력하세요) => ");
            reservationTime = sc.next();
            reservationTime.trim();
            if(reservationTime.equals("q"))
            {
                System.out.println("종료합니다.");
                System.exit(0);
            }

            currentTimeWithoutZero = getClearCurrentTime();  //현재 시간 입력에 쓸데없는 0을 많이 넣어도 모두 없애는 메서드 ex) 2022/00002/3 ->2022/2/3
            pathName = currentYear +"-"+ currentMonth +"-"+ currentDate;

            if (isReservationTimeNotIncludedScope()) continue;
            createReservationTxt();
            break;
        }
        return true;
    }

    private boolean isReservationTimeNotIncludedScope() {
        String[] splitReservationTime = getSplitReservationTime();
        if (splitReservationTime == null) return true;

        //ReservationTime
        //소수점입력 받았을 때 처리
        reservationYear = (int)Double.parseDouble(splitReservationTime[0]);//년도
        reservationMonth = (int)Double.parseDouble(splitReservationTime[1]);//달
        reservationDate = (int)Double.parseDouble(splitReservationTime[2]);//일
        reservationHour = (int)Double.parseDouble(splitReservationTime[3]);//시간
        reservationMinute = (int)Double.parseDouble(splitReservationTime[4]);//분

        //1월에서 12사이 인지/ 1970과 2037년 사이인지
        if (isYearAndMonthIncludedInScope()) return true;
        if (isDateIncludedInScope()) return true;
        if (isHourAndMinuteIncludedInScope()) return true;

        //현재보다 과거인지 / 현재 년도와 예약 년도가 3일 이상 차이나는지 계산합니다.
        if (isYearGapNotOk()) return true;
        if(currentYear == reservationYear)
        {
            if(currentMonth > reservationMonth) {
                System.out.println("기록된 month 보다 과거입니다");
                return true;
            }
            else
            {
                //현재 date 와 예약 date 가 3일 이상 차이나는지 계산합니다.
                if (isDateGapNotOk()) return true;
                //예약 시간의 시간과 분이 현재보다 과거인지 판별합니다.
                if(currentMonth == reservationMonth && reservationDate == currentDate )
                    if (isHourAndMinuteGapNotOk()) return true;
            }
        }
        return false;
    }

    private void createReservationTxt() {
        clearReservationTime = reservationYear +"-"+ reservationMonth +"-"+ reservationDate + "/" + reservationHour+":"+reservationMinute;
        File reserveFile = new File(reservationYear +"-"+ reservationMonth +"-"+ reservationDate);
        try {
            if(!reserveFile.exists())
            {
                reserveFile.mkdir();
                File visit = new File(reservationYear +"-"+ reservationMonth +"-"+ reservationDate+ "/visited.txt");
                File booked = new File(reservationYear +"-"+ reservationMonth +"-"+ reservationDate + "/booked.txt");

                visit.createNewFile();
                booked.createNewFile();

            }
        }catch(IOException e){
            e.printStackTrace();
            System.out.println("예약 파일이 없습니다.");
        }
    }

    private boolean isYearAndMonthIncludedInScope() {
        if(reservationYear < 1970){
            System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
            return true;
        }else if(reservationYear > 2037){
            System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
            return true;
        }
        if(reservationMonth > 12){
            System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
            return true;
        }else if(reservationMonth < 1){
            System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
            return true;
        }
        return false;
    }

    private boolean isHourAndMinuteIncludedInScope() {
        if(reservationHour < 8){
            System.out.println("영업시간은 08시부터입니다.");
            return true;
        }else if(reservationHour > 21){
            System.out.println("영업시간은 22시까지입니다.");
            return true;
        }
        if(reservationMinute != 0 && reservationMinute != 30)
        {
            System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
            return true;
        }
        return false;
    }

    private boolean isDateIncludedInScope() {
        if(reservationMonth ==1 || reservationMonth ==3 || reservationMonth ==5 || reservationMonth ==7 || reservationMonth ==8 || reservationMonth ==10 || reservationMonth ==12){
            if(reservationDate <1){
                System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                return true;
            }else if(reservationDate > 31){
                System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                return true;
            }
        }else if(reservationMonth ==2){
            System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
            if(reservationDate < 1){
                System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                return true;
            }else if(reservationDate > 28){
                System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                return true;
            }
        }else if(reservationMonth ==4 || reservationMonth ==6 || reservationMonth ==9 || reservationMonth ==11) {
            if(reservationDate < 1){
                System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                return true;
            }else if(reservationDate > 30){
                System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                return true;
            }
        }
        return false;
    }

    private boolean isDateGapNotOk() {
        int maxDateInCurrentMonth = 0;
        if(currentMonth==1 ||currentMonth==3 ||currentMonth==5 ||currentMonth==7 ||currentMonth==8||currentMonth==10 ||currentMonth==12){
            maxDateInCurrentMonth=31;
        }else if(currentMonth==4 ||currentMonth==6 ||currentMonth==9 ||currentMonth==11){
            maxDateInCurrentMonth=30;
        }else if(currentMonth==2){
            maxDateInCurrentMonth=28;
        }
        if(currentMonth== reservationMonth && (currentDate > reservationDate)) {
            System.out.println("기록된 date 보다 과거입니다");
            return true;
        }else if(currentMonth< reservationMonth && (maxDateInCurrentMonth-currentDate+ reservationDate) > 3){
            System.out.println("3일 이후는 예약할 수 없습니다.");
            return true;
        }
        else if(currentMonth== reservationMonth && reservationDate - currentDate > 3)
        {
            System.out.println("3일 이후는 예약할 수 없습니다.");
            return true;
        }
        return false;
    }

    private boolean isHourAndMinuteGapNotOk() {
        if(currentHour > reservationHour) {
            System.out.println("기록된 time 보다 과거입니다");
            return true;
        }
        else
        {
            // when years, months, dates and times are the same
            if(currentHour == reservationHour && currentMinute > reservationMinute) {
                System.out.println("기록된 time 보다 과거입니다");
                return true;
            }
        }
        return false;
    }

    private boolean isYearGapNotOk() {
        if(currentYear > reservationYear) {
            System.out.println("기록된 year 보다 과거입니다");
            return true;
        }
        else
        {
            if(reservationYear -currentYear > 1){
                System.out.println("3일 이후는 예약할 수 없습니다.");
                return true;
            }
            else if(reservationYear -currentYear == 1)
            {
                //2022/12/31 과 2023/1/1의 경우를 계산함 
                if(reservationMonth == 1 && currentMonth == 12)
                {
                    if(31-currentDate+ reservationDate > 3)
                    {
                        System.out.println("3일 이후는 예약할 수 없습니다.");
                        return true;
                    }
                }
                else
                {
                    System.out.println("3일 이후는 예약할 수 없습니다.");
                    return true;
                }
            }
        }
        return false;
    }

    private String[] getSplitReservationTime() {
        int hyphenNum = reservationTime.length() - reservationTime.replace("-","").length();
        int slashNum = reservationTime.length() - reservationTime.replace("/","").length();
        int twoDotNum = reservationTime.length() - reservationTime.replace(":","").length();
        String[] splitReservationTime = reservationTime.split("-|/|:");

        boolean loopFlag = false;
        outerLoop:
        for(int i=0; i<splitReservationTime.length; i++){
            splitReservationTime[i]=splitReservationTime[i].replaceFirst("^0+(?!$)", "");
            for(int j=0; j<splitReservationTime[i].length(); j++){
                if(splitReservationTime[i].charAt(j) == '.')
                {
                    System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                    loopFlag=true;
                    break outerLoop;
                }
                else if((int)splitReservationTime[i].charAt(j) <'0' || (int)splitReservationTime[i].charAt(j) >'9'){
                    System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                    loopFlag=true;
                    break outerLoop;
                }
            }
        }

        if(loopFlag){
            return null;
        }
        if(splitReservationTime.length != 5){
            System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
            return null;
        }
        if(hyphenNum != 2){
            System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
            return null;
        }
        if(slashNum != 1){
            System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
            return null;
        }
        if(twoDotNum != 1){
            System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
            return null;
        }
        return splitReservationTime;
    }

    private String getClearCurrentTime() {
        String[] splitCurrentTime = currentTime.split("[-/:]");
        for(int i=0; i<splitCurrentTime.length; i++) {
            splitCurrentTime[i] = splitCurrentTime[i].replaceFirst("^0+(?!$)", "");
        }
        currentYear = (int)Double.parseDouble(splitCurrentTime[0]);
        currentMonth = (int)Double.parseDouble(splitCurrentTime[1]);
        currentDate = (int)Double.parseDouble(splitCurrentTime[2]);
        currentHour = (int)Double.parseDouble(splitCurrentTime[3]);
        currentMinute = (int)Double.parseDouble(splitCurrentTime[4]);

        return currentYear +"-"+ currentMonth +"-"+ currentDate +"/"+ currentHour +":"+ currentMinute;
    }
}
