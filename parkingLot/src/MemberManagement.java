import java.io.*;

import java.util.Scanner;


public class MemberManagement {

    private String memberId;

    public MemberManagement() {
    }

    public MemberManagement(String memberId) {
        this.memberId = memberId;
    }

    public String registerMember() {

        Scanner scan = new Scanner(System.in);
        while(true) {
            System.out.println("회원 ID(전화번호)를 입력하세요. ( 입력 예시: 01043218765 )");
            System.out.print(">>>");
            String memberIdInput = scan.next();
            memberIdInput.trim();

            if (isCorrectMemberIdType(memberIdInput)) continue;
            createUserTxt();

            break;
        }
        return memberId; //memberId return
    }

    private void createUserTxt() {
        File userTxt = new File("User.txt");
        try {
            if (!userTxt.exists())
                userTxt.createNewFile();
        } catch (Exception e) {
            e.getStackTrace();
        }

        try
        {
            //파일에서 읽은 한라인을 저장하는 임시변수
            String thisLine = "";
            // 새 로그가 저장될 임시파일 생성
            File tmpFile = new File("aaaaaaaaaaa.txt");
            // 기존 파일
            FileInputStream currentFile = new FileInputStream("User.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(currentFile));
            // output 파일
            FileOutputStream fout = new FileOutputStream(tmpFile);
            PrintWriter out = new PrintWriter(fout);
            //파일 내용을 한라인씩 읽어 삽입될 라인이 오면 문자열을 삽입
            int k = 0;
            boolean isRegisteredMember = false;
            while ((thisLine = in.readLine()) != null) {
                if(thisLine.contains(memberId))
                    isRegisteredMember = true;
                out.println(thisLine);
                k++;
            }
            if (thisLine == null && k == 0)
                out.print(memberId);
            else if(!isRegisteredMember)
                out.println(memberId);
            out.flush();
            out.close();
            in.close();
            userTxt.delete();
            //임시파일을 원래 파일명으로 변경
            tmpFile.renameTo(userTxt);

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private boolean isCorrectMemberIdType(String memberIdInput) {
        if(memberIdInput.length() !=11)
        {
            System.out.println("잘못된 형식입니다. 다시 입력하세요");
            return true;
        }
        String firstPartPhoneNumber = memberIdInput.substring(0,3);
        String middlePartPhoneNumber = memberIdInput.substring(3,7);
        String lastPartPhoneNumber = memberIdInput.substring(7);

        if (!isOnlyNumberInId(firstPartPhoneNumber)) return true;
        if (!isOnlyNumberInId(middlePartPhoneNumber)) return true;
        if (!isOnlyNumberInId(lastPartPhoneNumber)) return true;
        if(!firstPartPhoneNumber.equals("010"))
        {
            System.out.println("010 형식으로 입력해주세요.");
            return true;
        }
        memberId = memberIdInput;
        return false;
    }

    private static boolean isOnlyNumberInId(String middlePartPhoneNumber) {
        char[] PartToCharArray = middlePartPhoneNumber.toCharArray();
        boolean isCorrectType = true;
        for (int i = 0; i < PartToCharArray.length; i++) {
            if(PartToCharArray[i]<'0' || PartToCharArray[i] >'9')
            {
                System.out.println("ID(전화번호)에는 숫자만 입력하실 수 있습니다.");
                isCorrectType = false;
                break;
            }
        }
        if(!isCorrectType)
        {
            return false;
        }
        return true;
    }

    //blackList 처리 : 노쇼 + 예약자리에서 안비키는 입차자
    public void manageBlackList() {

    }

    public boolean addNewCarToMember(String carNum) {
        StringBuffer sb = new StringBuffer();
        FileReader readFile;
        String getLine;
        File userTxt = new File("User.txt");
        boolean isNewCar = true;
        try {
            // 기존 파일
            FileInputStream currentFile = new FileInputStream(userTxt);
            BufferedReader in = new BufferedReader(new InputStreamReader(currentFile));
            //파일에서 읽은 한라인을 저장하는 임시변수
            // 새 로그가 저장될 임시파일 생성
            File tmpFile = new File("aaaaaaaaaaa.txt");
            // output 파일
            FileOutputStream fout = new FileOutputStream(tmpFile);
            PrintWriter out = new PrintWriter(fout);

            while ((getLine = in.readLine()) != null) {
                if (getLine.contains(memberId)) {
                    String[] infoSplit = getLine.split(" ");
                    if(getLine.contains(carNum))
                    {
                        System.out.println("이미 회원에 등록된 차량입니다. 환영합니다.");
                        isNewCar = false;
                        out.println(getLine);
                    }
                    else
                    {
                        String newCarListForMember = getLine +" "+ carNum;
                        out.println(newCarListForMember);

                    }

                }
                else
                    out.println(getLine);
            }
            out.flush();
            out.close();
            in.close();
            userTxt.delete();
            //임시파일을 원래 파일명으로 변경
            tmpFile.renameTo(userTxt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isNewCar;
    }
}
