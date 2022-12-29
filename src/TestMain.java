import java.text.ParseException;
import java.util.Scanner;

public class TestMain {
    public static void main(String[] args) throws ParseException {

        System.out.println("주차관리 시스템에 오신 걸 환영합니다.");
        CurrentTime currentTime = new CurrentTime();
        currentTime.setCurrentTime();
        String date_time = currentTime.getUserDateTimeInput();
        MemberManagement memberManagement = new MemberManagement();
        String memberId = memberManagement.registerMember();


        boolean flag = true;
        while(true){
            Scanner scan = new Scanner(System.in);


            int menu =0;
            boolean flag1 = true;
            while(flag1)
            {
                System.out.println("메뉴를 입력하세요");
                System.out.println("1)방문 2)예약 3)종료");
                System.out.print(">>>");
                String menuS = scan.nextLine();
                String[] split = menuS.split("");

                if(split.length != 1)
                {
                    System.out.println("잘못된 형식입니다. 다시 입력해주세요.");
                    continue;
                }

                if(split[0].equals("1") || split[0].equals("2") || split[0].equals("3"))
                {
                    menu = Integer.parseInt(split[0]);
                    break;
                }
                System.out.println("잘못된 형식입니다. 다시 입력해주세요.");
            }

            currentTime.setMenuNum(menu);
            currentTime.setting1();
            switch (menu) {
                case 1:
                    //방문()
                    Visit visit = new Visit(date_time,memberId);
                    visit.menu();
                    flag = false;
                    break;
                case 2:
                    Reservation reserve = new Reservation(date_time,memberId);
                    reserve.reservation();
                    flag = false;
                    break;
                case 3:
                    //종료()
                    flag = false;
                    break;
            }
            if(!flag) break;
        }

    }
}
