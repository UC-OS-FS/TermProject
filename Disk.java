package TermProject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/*
1. 문현균
2. 박민근
3. 박민근
4. 문현균
5. 손진우
6. 유승화
7. 유승화
8. 손진우
9. 이인홍
10. 이인홍
 */

class Console{
    private SuperBlock sb;
    private int select;
    private int num;
    private int size1,size2;
    private int count;
    private BufferedReader in =  new BufferedReader(new InputStreamReader(System.in));
    private Disk disk;

    Console(SuperBlock sb){
        this.sb = sb;
        select = -1;
        count = 0;
    }
    public void start(){
        while(select != 0){
            System.out.println("File System Management\n"
                    + "0.  Quit\t"
                    + "1.  Make disk\t"
                    + "2.  Format disk\t"
                    + "3.  Umount disk\t"
                    + "4.  Empty space in disk\t"
                    + "6.  Read file\t"
                    + "7.  Write file\t"
                    + "8.  Create file\t"
                    + "9.  Read file by parallel\t"
                    + "9.  Delete file\t"
                    + "10. Defragment");
            try{
                System.out.print("Choose number: ");
                select = Integer.parseInt(in.readLine());
                if(select > 10 || select < 0){
                    System.out.println("Out of boundary");
                    continue;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            switch(select){
                case 0: System.out.println("System 종료"); break; // 종료

                case 1: //초기화
                    System.out.println("Decide disk size(two integer): ");
                    try{
                        size1 = Integer.parseInt(in.readLine());
                        size2 = Integer.parseInt(in.readLine());
                        if(size1 <= 0 || size2 <= 0){
                            System.out.println("size must be bigger than 0");
                            break;
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    sb.diskVector.add(new Disk(size1,size2));
                    System.out.println("Disk is added successfully\n");
                    break;

                case 2: //format disk
                    disk = chooseDisk();
                    if(disk == null){
                        break;
                    }
                    disk.formatDisk();
                    System.out.println("Disk got formated successfully");
                    break;
                case 3: //unmount disk
                    disk = chooseDisk();
                    if(disk == null){
                        break;
                    }
                    sb.unmountDisk(disk);
                    System.out.println("Disk got unmounted successfully");
                    break;
                case 4: //엠티스페이스
                    disk = chooseDisk();
                    if(disk == null){
                        break;
                    }
                    System.out.println("Empty space in the disk: " + disk.getFreespace());
                    break;
                case 5: //리드
                    disk = chooseDisk();
                    if(disk == null){
                        break;
                    }
                    getInodeId(chooseDisk());
                    break;
                case 6: //라이트
                    disk = chooseDisk();
                    if(disk == null){
                        break;
                    }
                    getInodeId(chooseDisk());
                    break;
                case 7: //크리에이트
                    disk = chooseDisk();
                    if(disk == null){
                        break;
                    }
                    chooseDisk();
                    break;
                case 8: //페러럴 리드
                    disk = chooseDisk();
                    if(disk == null){
                        break;
                    }
                    getInodeId(chooseDisk());
                    break;
                case 9: // 딜리트
                    disk = chooseDisk();
                    if(disk == null){
                        break;
                    }
                    getInodeId(chooseDisk());
                    break;
                case 10: //디프레그먼트
                    disk = chooseDisk();
                    if(disk == null){
                        break;
                    }
                    getInodeId(chooseDisk());
                    break;
            }
        }
    }
    private Disk chooseDisk(){
        System.out.println("Choose a disk");
        for(int i = 0; i< sb.diskVector.size(); i++){
            System.out.print(i+ ". " + sb.diskVector.elementAt(i).id + " ");
        }
        System.out.println();
        try{
            System.out.print("Choose number: ");
            num = Integer.parseInt(in.readLine());
            if(num >= sb.diskVector.size() || num < 0){
                System.out.println("Wrong number");
                return null;
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return sb.diskVector.elementAt(num);
    }
    private int getInodeId(Disk disk){
        System.out.println("Choose a file");
        for(int i = 0; i< disk.iNodeIDVector.size(); i++){
            System.out.print(i+ ": " + disk.iNodeIDVector.elementAt(i) + " ");
        }
        System.out.println();
        try{
            System.out.print("Choose number: ");
            num = Integer.parseInt(in.readLine());
            if(num >= disk.iNodeIDVector.size() || num < 0){
                System.out.println("Wrong number");
                return -1;
            }
        }catch(Exception e){
            e.printStackTrace();
            return -1;
        }
        return disk.iNodeIDVector.elementAt(num);
    }
}

//전역변수 처럼 사용하기 위함.
class Global {
    public static int diskID = 0;
    public static int iNodeID = 0;
    public static Vector<INODE> iNodeVector = new Vector<>();
    public static INODE getINODE(Integer id) {
        for (int i = 0; i < iNodeVector.size(); i++) {
            if (Integer.compare(iNodeVector.get(i).id, id) == 0)
                return iNodeVector.get(i);
        }
        return null;
    }
}

class INODE {
    public final Integer id;
    public Integer diskID;      //default null
    public String owner;        //default null
    public int startPoint;      //default -1
    public int size;            //default 0
    public boolean readOnly;    //default false

    INODE() {
        id = new Integer(Global.iNodeID++);
        startPoint = -1;
        Global.iNodeVector.add(this);
    }
}

class SuperBlock {
    Vector<Disk> diskVector;
    SuperBlock() {
        diskVector = new Vector<>();
    }
    public Disk getDisk(Integer id) {
        for (int i = 0; i < diskVector.size(); i++) {
            if (Integer.compare(diskVector.get(i).id, id) == 0)
                return diskVector.get(i);
        }
        return null;
    }
    void unmountDisk(Disk disk){
        diskVector.remove(disk);

        for(int tmp :disk.iNodeIDVector){
            Global.iNodeVector.remove(Global.getINODE(tmp));
        }
    }
}
public class Disk {
    public final Integer id;
    Vector<Integer> iNodeIDVector;
    int[][] blocks;

    //1. Initialize
    Disk(int numBlocks, int blockSize) {
        id = new Integer(Global.diskID++);
        iNodeIDVector = new Vector<>();
        blocks = new int[numBlocks][blockSize];
    }

    int getNumBlocks() {
        return blocks == null ? 0 : blocks.length;
    }

    int getBlockSize() {
        return getNumBlocks() > 0 ? blocks[0].length : 0;
    }
    void formatDisk(){
//        if(!vec.contains(inodeId)){
//            System.out.println("There is no requested file");
//            return;
//        }
//        if(Global.iNodeHashMap.get(inodeId).readOnly){
//            System.out.println("You don't have permission");
//            return;
//        }
        for(int tmp :iNodeIDVector){
            Global.iNodeVector.remove(Global.getINODE(tmp));
        }
        iNodeIDVector.clear();
    }

    //4. Empty space
    int getFreespace() {
        return getNumBlocks()*getBlockSize()
                - iNodeIDVector.stream().map(i -> Global.getINODE(i).size).mapToInt(i -> i).sum();
    }

    public static void main(String[] args) {
        SuperBlock sb = new SuperBlock();

        Console c = new Console(sb);
        c.start();
    }
}













