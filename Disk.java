/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TermProject;
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

    void assignBuffer(int startPoint, int inputSize, String[] elem){
        // From start point, write
        for (int i = startPoint, bufferIdx = 0; i < startPoint + inputSize; i++, bufferIdx++){
            int row = i / getBlockSize();
            int col = i % getBlockSize();

            blocks[row][col] = Integer.parseInt(elem[bufferIdx]);
        }
    }

    void create(String[] elem){


        int inputSize = elem.length;

        // Find start point
        int startPoint = findStartPoint(inputSize);
        if (startPoint == -1 ) {
            System.out.println("Cannot find start point, too big file or defragment is needed");
            return;
        }

        assignBuffer(startPoint, inputSize, elem);

        // Create new INODE
        INODE create = new INODE();
        create.startPoint = startPoint;
        create.size = inputSize;
        iNodeIDVector.add(create.id);

    }

    void write(int fileID, String[] elem){

        if (Global.getINODE(fileID).readOnly) return;

        int fileStartPoint = Global.getINODE(fileID).startPoint;
        int fileSize = Global.getINODE(fileID).size;
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
            assignBuffer(fileStartPoint + fileSize, inputSize, elem);

            int newSize = fileSize + inputSize;
            Global.getINODE(fileID).size = newSize;
        }
    }

    public static String[] scanInput(){
        // Get Inputs ( divided by space )
        System.out.print("Enter numbers: ");
        Scanner scan = new Scanner(System.in);
        String inputs;
        inputs = scan.nextLine();
        String elem[] = inputs.split(" ");  // Inputs as string array

        return elem;
    }



    public static void main(String[] args) {

        Disk disk1 = new Disk(100, 100);
        Disk disk2 = new Disk(100, 100);

        String[] elem = scanInput();
        disk1.create(elem);
        //disk2.create(elem);
        disk1.write(0, scanInput());

        System.out.println("-------INODE ID---------");
        for (int i = 0; i < Global.iNodeVector.size(); i++){
            System.out.print(Global.iNodeVector.elementAt(i).id);
            System.out.println(" ");
        }

        System.out.println(disk1.getFreespace());
        

        
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
    }
}