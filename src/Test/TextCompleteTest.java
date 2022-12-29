package Test;

import java.io.*;
import java.nio.file.*;

public class TextCompleteTest {

    public static void main(String[] args) throws Exception{


        //-- 파일 생성 코드-----------------------
        String currentTime = "2020-10-03/14:01";
        String[] split = currentTime.split("/");
        String date = split[0];
        String path = System.getProperty("user.dir"); //현재 파일 경로 가져오기

        //--현재 타임 로그 저장---
        File timeLog = new File("timeLog.txt");
        try{
            FileOutputStream fTimeLong= new FileOutputStream("timeLog.txt",true);
            String log = "\n" +currentTime;
            //파일에서 읽은 한라인을 저장하는 임시변수
            String thisLine = "";
            // 새 로그가 저장될 임시파일 생성
            File tmpFile = new File("$$$$$$$$.txt");
            // 기존 파일
            FileInputStream currentFile = new FileInputStream("timeLog.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(currentFile));
            // output 파일
            FileOutputStream fout = new FileOutputStream(tmpFile);
            PrintWriter out = new PrintWriter(fout);
            int i = 0;
            //파일 내용을 한라인씩 읽어 삽입될 라인이 오면 문자열을 삽입
            while ((thisLine = in.readLine()) != null) {
                if (i == 0)
                    out.println(currentTime);
                out.println(thisLine);
                i++;
            }
            out.flush();
            out.close();
            in.close();
            timeLog.delete();
            //임시파일을 원래 파일명으로 변경
            tmpFile.renameTo(timeLog);

        }catch (Exception e)
        {
            e.getStackTrace();
        }

        RandomAccessFile file = new RandomAccessFile("timeLog.txt","r");
        long fileSize = file.length();
        long pos;

        //--날짜별 폴더 생성--
        File dir = new File(split[0]);
        if(!dir.exists())
        {
            dir.mkdir();	//폴더 만들기
            File visit = new File(split[0] + "/visited.txt");


            File reservation = new File(split[0] + "/booked.txt");
            try{
                visit.createNewFile();
                reservation.createNewFile();

                timeLog.createNewFile();



            }catch(IOException e){
                e.printStackTrace();
            }
        }

        //--txt 입력
        int menu = 1;
        if(dir.exists() && menu == 1)
        {
            try{
                FileOutputStream fVisited= new FileOutputStream(split[0] + "/visited.txt",true);
                String parkingArea = "A-1-5";
                String carNum = "221-차-3211";

                String fullString = "\n" + parkingArea + " " + carNum + " " + currentTime;
                fVisited.write(fullString.getBytes());
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else if(dir.exists() && menu == 2)
        {
            try{
                FileOutputStream fVisited= new FileOutputStream(split[0] + "/booked.txt",true);
                String parkingArea = "A-1-5";
                String carNum = "221-차-3211";

                String fullString = "\n" + parkingArea + " " + carNum + " " + currentTime;
                fVisited.write(fullString.getBytes());
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }






    }

}
