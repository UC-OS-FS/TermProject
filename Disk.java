package TermProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Collections;
import java.util.Vector;
import java.util.Scanner;
import java.util.HashMap;
import java.util.List;
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
            System.out.println("\n\nFile System Management\n"
                    + "0.  Quit\t"
                    + "1.  Make disk\t"
                    + "2.  Format disk\t"
                    + "3.  Umount disk\t"
                    + "4.  Empty space in disk\t"
                    + "5.  Read file\t"
                    + "6.  Write file\t"
                    + "7.  Create file\t"
                    + "8.  Read file by parallel\t"
                    + "9.  Delete file\t"
                    + "10. Defragment\t"
                    + "11. Show disk list\t"
                    + "12. Show File list");
            try{
                System.out.print("Choose menu: ");
                select = Integer.parseInt(in.readLine());
                if(select > 12 || select < 0){
                    System.out.println("Out of boundary");
                    continue;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            switch(select){
                case 0: System.out.println("System Quit"); break; // 종료

                case 1: //초기화
                    try{
                        System.out.println("Decide disk number of blocks: ");
                        size1 = Integer.parseInt(in.readLine());
                        System.out.println("Decide disk blockSize: ");
                        size2 = Integer.parseInt(in.readLine());
                        if(size1 <= 0 || size2 <= 0){
                            System.out.println("size must be bigger than 0");
                            break;
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    disk = new Disk(size1,size2);
                    sb.diskVector.add(disk);
                    System.out.println("Disk"+disk.id+" is added successfully\n");
                    break;

                case 2: //format disk
                    disk = chooseDisk();
                    if(disk == null){
                        break;
                    }
                    //disk.formatDisk();
                    System.out.println("Disk got formated successfully");
                    break;
                case 3: //unmount disk
                    disk = chooseDisk();
                    if(disk == null){
                        break;
                    }
                    //sb.unmountDisk(disk);
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
                    }/*
                    getInodeId(chooseDisk());*/
                    //----------------------------------------------------------------------------------

                    int[] buffer;

                    //Scanner scan = new Scanner(System.in);
                    int index = 0; // 시작점
                    int size = 0; // 크기

                    try {
                        System.out.println("Enter the index :");
                        index = Integer.parseInt(in.readLine());
                        System.out.println("Enter the size :");
                        size = Integer.parseInt(in.readLine());
                    } catch (NumberFormatException | IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
//                	index = scan.nextInt();
//                	size = scan.nextInt();

                    buffer = new int[size];

                    System.out.println("Index and size values are entered.");

                    buffer = disk.readDisk(index, size);
                    //return 받은 결과물 출력
                    if(buffer != null)
                    {
                        for(int k=0; k < buffer.length; k++)
                        {
                            System.out.println(buffer[k] + " ");
                        }
                    }
                    else
                    {
                        System.out.println("Buffer is null");
                    }
                    //scan close 시 에러 발생...
                    //scan.close();
                    break;
                // ---------------------------------------------------------------------
                case 6: //라이트
                    disk = chooseDisk();
                    if(disk == null){
                        break;
                    }

                    Integer inodeID = getInodeId(disk);
                    if (inodeID < 0)
                        continue;

                    disk.write(inodeID, Disk.scanInput());

                    break;
                case 7: //크리에이트
                    disk = chooseDisk();
                    if(disk == null){
                        break;
                    }

                    try {
                        System.out.print("Enter owner: ");
                        String _owner = in.readLine();

                        System.out.print("Enter readOnly permission(0, 1): ");
                        boolean _readOnly = in.readLine().equals("1");

                        disk.create(_owner, _readOnly, Disk.scanInput());

                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("File create fail");
                    }

                    break;
                case 8: //페러럴 리드
                    disk = chooseDisk();
                    if(disk == null){
                        break;
                    }
                    int threadNum = 0;
                    try {
                        System.out.println("Enter the number of thread you want to create :");
                        threadNum = Integer.parseInt(in.readLine());
                    }
                    catch (NumberFormatException | IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    for(int i=0; i<threadNum; i++)
                    {
                        new Thread("" + i)
                        {
                            public void run()
                            {
                                int[] buffer;

                                int index; // 시작점
                                int size; // 크기

                                for (int i = 0; i < disk.iNodeIDVector.size(); i++) //조건 맞는 Disk inodeIDVector에서 찾는 루프
                                {
                                    index = Global.getINODE(disk.iNodeIDVector.get(i)).startPoint;
                                    size = Global.getINODE(disk.iNodeIDVector.get(i)).size;
                                    buffer = new int[size];
                                    for(int j = 0; j < size; j++)
                                    {
                                        buffer[j] = disk.blocks[(j + index) / disk.getBlockSize()][(j + index) % disk.getBlockSize()];
                                        System.out.println("Thread " + getName() + " value : " + buffer[j] + " ");
                                    }
                                }
                            }
                        }.start();
                    }
                    break;
                case 9: // 딜리트
                    disk = chooseDisk();
                    if(disk == null){
                        break;
                    }
                    disk.delete(getInodeId(disk));
                    break;
                case 10: //디프레그먼트
                    disk = chooseDisk();
                    if(disk == null){
                        break;
                    }
                    disk.defragment();
                    break;
                case 11: //Show List
                    System.out.println("Disk id List");
                    sb.diskVector.forEach(disk1 -> {System.out.print(disk1.id + "\t");});
                    System.out.println();
                    break;
                case 12: //Show List
                    disk = chooseDisk();
                    if(disk == null){
                        break;
                    }
                    for (Integer integer : disk.iNodeIDVector) {
                        INODE tempINODE = Global.getINODE(integer);
                        System.out.println("ID: " + tempINODE.id + "\towner: " + tempINODE.owner + "\treadonly: "
                                + tempINODE.readOnly + "\tdiskID: " + tempINODE.diskID
                                + "\tstartPoint: " + tempINODE.startPoint + "\tsize: " + tempINODE.size);
                    }
                    break;
            }
        }
    }
    private Disk chooseDisk(){
        System.out.println("\nChoose a disk");
        for(int i = 0; i< sb.diskVector.size(); i++){
            System.out.print(i+ ". Disk" + sb.diskVector.elementAt(i).id + "\t");
        }
        System.out.println();
        try{
            System.out.print("Choose menu: ");
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
    private Integer getInodeId(Disk disk){
        System.out.println("Choose a file");
        if (disk.iNodeIDVector.isEmpty()) {
            System.out.println("There is no file");
            return -1;
        }

        for(int i = 0; i< disk.iNodeIDVector.size(); i++){
            System.out.print(i+ ". FILE" + disk.iNodeIDVector.elementAt(i) + "\t");
        }
        System.out.println();
        try{
            System.out.print("Choose menu: ");
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

    public static INODE getINODE(Integer id)
    {
        for (int i = 0; i < iNodeVector.size(); i++)
        {
            if (Integer.compare(iNodeVector.get(i).id, id) == 0)
                return iNodeVector.get(i);
        }
        return null;
    }
}

//id, startpoint, size 포함
class INODE
{
    public final Integer id;
    public Integer diskID;      //default null
    public String owner;        //default null
    public int startPoint;      //default -1
    public int size;            //default 0
    public boolean readOnly;    //default false

    INODE()
    {
        id = new Integer(Global.iNodeID++);
        startPoint = -1;
        Global.iNodeVector.add(this);
    }
}

class SuperBlock
{
    Vector<Disk> diskVector;
    SuperBlock()
    {
        diskVector = new Vector<>();
    }
    public Disk getDisk(Integer id)
    {
        for (int i = 0; i < diskVector.size(); i++)
        {
            if (Integer.compare(diskVector.get(i).id, id) == 0)
                return diskVector.get(i);
        }
        return null;
    }
}

public class Disk extends Thread
{
    public final Integer id;
    Vector<Integer> iNodeIDVector;
    int[][] blocks;

    //0. Thread - Run
    public void run()
    {
        System.out.println(Thread.currentThread().getName());
        Scanner scan = new Scanner(System.in);
        int index; // 시작점
        int size; // 크기

        System.out.println("Thread :: Enter the index and size :");
        index = scan.nextInt();
        size = scan.nextInt();

        if(this.readDisk(index, size) != null)
        {
            for(int i=0; i<this.readDisk(index, size).length; i++)
            {
                System.out.println(this.readDisk(index, size)[i]);
            }
        }
        scan.close();
    }

    //1. Initialize
    Disk(int numBlocks, int blockSize) {
        id = new Integer(Global.diskID++);
        iNodeIDVector = new Vector<>();
        blocks = new int[numBlocks][blockSize];
    }
    void temp_initial() // 임시 값 부여
    {
        int k = 0;
        for(int i=0; i<this.getNumBlocks();i++) // i : 0 ~ 11
        {
            for(int j=0; j<this.getBlockSize(); j++)
            {
                blocks[i][j] = k;
                k++;

            }
        }
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

    //5. Read disk
    int[] readDisk (int index, int size)
    {
        int[] buffer = new int[size];

        for (int i = 0; i < this.iNodeIDVector.size(); i++) //조건 맞는 Disk inodeIDVector에서 찾는 루프
        {
            //입력받은 인덱스 값이 StartPoint 와 SP + Size 사이일 경우
            if(Global.getINODE(this.iNodeIDVector.get(i)).startPoint + Global.getINODE(this.iNodeIDVector.get(i)).size >= index && Global.getINODE(this.iNodeIDVector.get(i)).startPoint <= index)
            {
                // 입력받은 인덱스 + 크기 <= StartPoint + Size
                if(Global.getINODE(this.iNodeIDVector.get(i)).startPoint + Global.getINODE(this.iNodeIDVector.get(i)).size >= index + size)
                {
                    for(int j = 0; j < size; j++)
                    {
                        buffer[j] = this.blocks[(j + index) / this.getBlockSize()][(j + index) % this.getBlockSize()];
                    }
                    return buffer;
                }
            }
        }
        System.out.println("Error : Out of range");
        return null;
    }

    int getSizeByID(int fileID){
        return Global.getINODE(fileID).size;
    }

    int findStartPoint(int inputSize){
        //Set keys = fileMap.keySet();
        //List list = new ArrayList(keys);
        //Collections.sort(list);


        // sorted ID Vector
        Vector<Integer> spVector = new Vector(iNodeIDVector.size());
        //Vector<Integer> sizeVector = new Vector(iNodeIDVector.size());
        HashMap<Integer, Integer> spIDMap = new HashMap();

        // sorted start point vector
        for (int i = 0; i < iNodeIDVector.size(); i++){
            spVector.add(Global.getINODE(iNodeIDVector.elementAt(i)).startPoint);
            spIDMap.put(spVector.elementAt(i), iNodeIDVector.elementAt(i));
        }

        Collections.sort(spVector);
        
        
            /*
        // sorted size vector
        for (int i = 0; i < iNodeIDVector.size(); i++){
            for (int j = 0; j < iNodeIDVector.size(); i++){
                if(spVector.elementAt(j) == Global.iNodeVector.elementAt(j).startPoint){
                    sizeVector.add(Global.iNodeVector.elementAt(j).size);
                    break;
                }
            }
        }
        */

        int beginning = 0;
        int end = getBlockSize() * getNumBlocks();

        if (spVector.size() == 0 && end > inputSize ) return 0;
        else {

            for (int i = 0; i < spVector.size(); i ++){
                if (i == 0){
                    if (inputSize <= spVector.elementAt(i) - beginning)

                        return beginning;
                }

                if (i == spVector.size() - 1){
                    if(inputSize <= end - (spVector.elementAt(i) + getSizeByID(spIDMap.get(spVector.elementAt(i)))))  // 끝 - (시작 + 사이즈)
                        return (spVector.elementAt(i) + getSizeByID(spIDMap.get(spVector.elementAt(i))));

                }

                if (i > 0) {
                    if( inputSize <=  spVector.elementAt(i) - (spVector.elementAt(i-1) + getSizeByID(spIDMap.get(spVector.elementAt(i-1))))) // 현재 파일 시작 - ( 이전 파일 시작 + 이전 파일 사이즈 )
                        return (spVector.elementAt(i-1) + getSizeByID(spIDMap.get(spVector.elementAt(i-1))));
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

    void assignBuffer(int startPoint, int inputSize, int[] elem){
        // From start point, write
        for (int i = startPoint, bufferIdx = 0; i < startPoint + inputSize; i++, bufferIdx++){
            int row = i / getBlockSize();
            int col = i % getBlockSize();

            blocks[row][col] = elem[bufferIdx];
        }
    }

    int[] copy(int startPoint, int size){
        int[] arr = new int[size];

        for(int i = 0; i < size; i++){
            int row = i / getBlockSize();
            int col = i % getBlockSize();
            arr[i] = blocks[row][col];
        }

        return arr;
    }
    void create(String _owner, boolean _readOnly, String[] elem){
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
        create.diskID = id;
        create.startPoint = startPoint;
        create.size = inputSize;
        create.owner = _owner;
        create.readOnly = _readOnly;
        iNodeIDVector.add(create.id);
        System.out.println("File created successfully");
    }

    void write(int fileID, String[] elem){

        if (fileID < 0)
            return;

        if (Global.getINODE(fileID).readOnly) {
            System.out.println("This file is readOnly");
            return;
        }

        int fileStartPoint = Global.getINODE(fileID).startPoint;
        int fileSize = Global.getINODE(fileID).size;
        int inputSize = elem.length;

        // sorted Vectors
        Vector<Integer> spVector = new Vector(iNodeIDVector.size());


        // sorted start point vector
        for (int i = 0; i < iNodeIDVector.size(); i++){
            spVector.add(Global.getINODE(iNodeIDVector.elementAt(i)).startPoint);
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
        if(space < inputSize) {
            int newStartPoint = findStartPoint(fileSize + inputSize);

            if (newStartPoint == -1 ){
                System.out.println("cannot write anymore");
                return;
            }
            else{
                int[] copy = copy(fileStartPoint, fileSize);
                assignBuffer(newStartPoint, fileSize, copy);
                assignBuffer(newStartPoint + fileSize, inputSize, elem);

                Global.getINODE(fileID).startPoint = newStartPoint;
            }
        }
        else{
            assignBuffer(fileStartPoint + fileSize, inputSize, elem);
        }
        int newSize = fileSize + inputSize;
        Global.getINODE(fileID).size = newSize;
        System.out.println("File written successfully");
    }

    public static String[] scanInput(){
        // Get Inputs ( divided by space )
        System.out.print("Enter numbers(separated by space): ");
        Scanner scan = new Scanner(System.in);
        String inputs;
        inputs = scan.nextLine();
        String elem[] = inputs.split(" ");  // Inputs as string array

        return elem;
    }



    void delete(Integer iNodeID) {
        if (iNodeID < 0) {
            return;
        }
        if (Global.getINODE(iNodeID).readOnly) {
            System.out.println("This file is readOnly");
            return;
        }
        Global.iNodeVector.remove(Global.getINODE(iNodeID));
        iNodeIDVector.remove(iNodeIDVector.indexOf(iNodeID));
        System.out.println("File deleted Sucssesfully");

    }

    void defragment() {

        Vector<Integer> temp = new Vector<>();
        Vector<INODE> sortedvecter = new Vector<>();
        for(int i=0; i<Global.iNodeVector.size(); i++) {
            if(Global.iNodeVector.get(i).diskID == this.id) {
                temp.add(Global.iNodeVector.get(i).startPoint);
            }
        }
        Collections.sort(temp);

        for(int i=0; i<temp.size(); i++) {
            for(int j=0; j<Global.iNodeVector.size(); j++) {
                if(Global.iNodeVector.get(j).startPoint == temp.get(i)) {
                    sortedvecter.add(Global.iNodeVector.get(j));
                }
            }
        }

        for(int i=0; i<sortedvecter.size(); i++) {
            if(i==0) {
                if(sortedvecter.get(i).startPoint != 0) {
                    sortedvecter.get(i).startPoint = 0;
                }
            }
            else {
                if(sortedvecter.get(i-1).startPoint+sortedvecter.get(i-1).size != sortedvecter.get(i).startPoint) {
                    sortedvecter.get(i).startPoint = sortedvecter.get(i-1).startPoint+sortedvecter.get(i-1).size;
                }
            }
        }

        for(int i=0; i<sortedvecter.size(); i++) {
            for(int j=0; j<Global.iNodeVector.size(); j++) {
                if(Global.iNodeVector.get(j).id == sortedvecter.get(i).id) {
                    for(int k=0; k<sortedvecter.get(i).size; k++) {
                        int point1 = temp.get(i) + k;
                        int point2 = Global.iNodeVector.get(j).startPoint + k;
                        int row1 = point1 / getBlockSize();
                        int col1 = point1 % getBlockSize();
                        int row2 = point2 / getBlockSize();
                        int col2 = point2 % getBlockSize();
                        blocks[row2][col2] = blocks[row1][col1];
                    }
                }
            }
        }
        System.out.println("Defragment Sucssesfully");
    }

    public static void main(String[] args) {
        Console c = new Console(new SuperBlock());
        c.start();

    }
}