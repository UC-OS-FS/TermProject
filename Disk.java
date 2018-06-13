/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TermProject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.Collections;
import java.util.Scanner;


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

    int findStartPoint(int inputSize){
        //Set keys = fileMap.keySet();
        //List list = new ArrayList(keys);
        //Collections.sort(list);


        // sorted ID Vector
        Vector<Integer> spVector = new Vector(iNodeIDVector.size());
        Vector<Integer> sizeVector = new Vector(iNodeIDVector.size());




        // sorted start point vector
        for (int i = 0; i < iNodeIDVector.size(); i++){

            spVector.add(Global.iNodeVector.elementAt(iNodeIDVector.elementAt(i)).startPoint);
        }

        Collections.sort(spVector);


        // sorted size vector
        for (int i = 0; i < iNodeIDVector.size(); i++){
            for (int j = 0; j < iNodeIDVector.size(); i++){
                if(spVector.elementAt(j) == Global.iNodeVector.elementAt(j).startPoint){
                    sizeVector.add(Global.iNodeVector.elementAt(j).size);
                    break;
                }
            }
        }

        int beginning = 0;
        int end = getBlockSize() * getNumBlocks();

        if (spVector.size() == 0 && end > inputSize ) return 0;
        else {

            for (int i = 0; i < spVector.size(); i ++){
                if (i == 0){
                    if (inputSize < spVector.elementAt(i) - beginning)

                        return beginning;
                }

                if (i == spVector.size() - 1){
                    if(inputSize < end - (spVector.elementAt(i) + sizeVector.elementAt(i)))  // 끝 - (시작 + 사이즈)
                        return (spVector.elementAt(i) + sizeVector.elementAt(i));

                }

                if (i > 0) {
                    if( inputSize <  spVector.elementAt(i) - (spVector.elementAt(i-1) + sizeVector.elementAt(i-1))) // 현재 파일 시작 - ( 이전 파일 시작 + 이전 파일 사이즈 )
                        return (spVector.elementAt(i-1) + sizeVector.elementAt(i-1));
                }
            }

        }
        return -1;
    }


    void create(){

        // Get Inputs ( divided by space )
        Scanner scan = new Scanner(System.in);
        String inputs;
        inputs = scan.nextLine();
        String elem[] = inputs.split(" ");  // Inputs as string array
        int inputSize = elem.length;

        // Find start point
        int startPoint = findStartPoint(inputSize);
        if (startPoint == -1 ) {
            System.out.println("Cannot find start point, too big file or defragment is needed");
            return;
        }


        // From start point, write
        for (int i = startPoint, bufferIdx = 0; i < startPoint + inputSize; i++, bufferIdx++){
            int row = i / getBlockSize();
            int col = i % getBlockSize();

            blocks[row][col] = Integer.parseInt(elem[bufferIdx]);
        }

        // Create new INODE
        INODE create = new INODE();
        create.startPoint = startPoint;
        create.size = inputSize;
        iNodeIDVector.add(create.id);

    }

    void write(int fileID){

        if (Global.getINODE(fileID).readOnly) return;

        int fileStartPoint = Global.getINODE(fileID).startPoint;
        int fileSize = Global.getINODE(fileID).size;

        // Input 
        Scanner scan = new Scanner(System.in);
        String inputs;
        inputs = scan.nextLine();
        String elem[] = inputs.split(" ");  // Inputs as string array
        int inputSize = elem.length;

        // sorted Vectors
        Vector<Integer> spVector = new Vector(iNodeIDVector.size());


        // sorted start point vector
        for (int i = 0; i < iNodeIDVector.size(); i++){
            spVector.add(Global.iNodeVector.elementAt(iNodeIDVector.elementAt(i)).startPoint);
        }
        Collections.sort(spVector);


        // Find index in spVector
        int idx = 0;  // idx_in_SpVector
        for (int i = 0; i < spVector.size(); i++){
            if (fileStartPoint == spVector.elementAt(i)){
                idx = i;
                break;
            }
        }

        // Find free space to append
        int space = 0;
        if (fileStartPoint == spVector.elementAt(spVector.size() - 1)){
            space = getBlockSize() * getNumBlocks() - (fileStartPoint + fileSize);
        }
        else {
            space = spVector.elementAt(idx+1) - (fileStartPoint + fileSize);
        }


        // Assign buffer on disk 
        if(space < inputSize) System.out.println("cannot write anymore");
        else{
            for (int i = fileStartPoint + fileSize, bufferIdx = 0; i < fileStartPoint + fileSize + inputSize; i++, bufferIdx++){
                int row = i / getBlockSize();
                int col = i % getBlockSize();

                blocks[row][col] = Integer.parseInt(elem[bufferIdx]);
            }

            int newSize = fileSize + inputSize;
            Global.getINODE(fileID).size = newSize;
        }
    }



    public static void main(String[] args) {

        /*
        Disk disk1 = new Disk(100, 100);
        Disk disk2 = new Disk(100, 100);
        disk1.create();
        disk2.create();
        disk1.write(0);

        System.out.println("-------INODE ID---------");
        for (int i = 0; i < Global.iNodeVector.size(); i++){
            System.out.print(Global.iNodeVector.elementAt(i).id);
            System.out.println(" ");
        }

        System.out.println(disk1.getFreespace());
        */

        
        /*
        SuperBlock sb = new SuperBlock();

        // 디스크 생성
        Disk disk1 = new Disk(3, 4);
        Disk disk2 = new Disk(3, 4);

        // 디스크 삽입
        sb.diskVector.add(disk1);
        sb.diskVector.add(disk2);

        // iNode 생성
        INODE iNode1 = new INODE();
        INODE iNode2 = new INODE();

        // iNode 삽입
        sb.diskVector.firstElement().iNodeIDVector.add(iNode1.id);
        sb.diskVector.firstElement().iNodeIDVector.add(iNode2.id);

        // create 대체
        iNode1.startPoint = 0;
        iNode1.size = 7;
        iNode2.startPoint = 9;
        iNode2.size = 1;

        System.out.println(sb.diskVector.firstElement().getFreespace());
        System.out.println(sb.diskVector.lastElement().getFreespace());

        // iNode의 정보를 바꾸었을때 sb에서 iNode의 id를 가지고 온 후 Global에서 조회
        iNode1.owner = "java";
        iNode2.owner = "os";
        for (int i = 0; i < sb.diskVector.firstElement().iNodeIDVector.size(); i++) {
            System.out.println(Global.getINODE(sb.diskVector.firstElement().iNodeIDVector.get(i)).owner);
        }
        */

        Console c = new Console(new SuperBlock());
        c.start();
    }
}