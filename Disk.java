/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TermProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Collections;
import java.util.Vector;
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
                    getInodeId(chooseDisk());
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


class INODE {
//id, startpoint, size 포함
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

public class Disk  {
    public final Integer id;
    Vector<Integer> iNodeIDVector;
    int[][] blocks;

    //1. Initialize
    Disk(int numBlocks, int blockSize) {
        id = new Integer(Global.diskID++);
        iNodeIDVector = new Vector<>();
        blocks = new int[numBlocks][blockSize];
    }
    void temp_initial()
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
            spVector.add(Global.getINODE(iNodeIDVector.elementAt(i)).startPoint);
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
            System.out.println("File written successfully");
        }
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
    
    void degfragment() {
        
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

        /*
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
        */

        
        /*
        SuperBlock sb = new SuperBlock();

    
    //5. Read disk
    int[] readDisk(int index, int size)
    {
    	int[] buffer;
    	buffer = new int[size];
    	
    	for(int i = 0; i < size; i++)
		{
			buffer[i] = this.blocks[(i + index) / this.getBlockSize()][(i + index) % this.getBlockSize()];
		}
    	    	
    	return buffer;
    }

    public static void main(String[] args)
    {
        SuperBlock sb = new SuperBlock(); // 슈퍼블록 할당


        // 디스크 생성
        Disk disk1 = new Disk(3, 4); // NumBlock * BlockSize
        //Disk disk2 = new Disk(4, 5); // 
        
        // diskVector에 disk 1, 2 삽입
        sb.diskVector.add(disk1);        
        sb.diskVector.elementAt(0).temp_initial(); // 값 확인 위한 임시값 대입
        
        //sb.diskVector.add(disk2);
        //sb.diskVector.lastElement().temp_initial(); // 값 확인 위한 임시값 대입
        
        // 새 iNode 생성
        INODE iNode1 = new INODE();
        //INODE iNode2 = new INODE();
        
        // iNode 삽입
        sb.diskVector.elementAt(0).iNodeIDVector.add(iNode1.id);        
        //sb.diskVector.elementAt(1).iNodeIDVector.add(iNode2.id);
        
        
        // create 대체
        iNode1.startPoint = 3;
        iNode1.size = 9;
      
        //iNode2.startPoint = 9;
        //iNode2.size = 7;       
        		
        for(int i=0; i<sb.diskVector.size(); i++)
        {
	        System.out.println(sb.diskVector.get(i).getFreespace());
        }
        
        // iNode의 정보를 바꾸었을때 sb에서 iNode의 id를 가지고 온 후 Global에서 조회
        iNode1.owner = "iNode1 Owner";
        //iNode2.owner = "iNode2 Owner";
        
        for (int i = 0; i < sb.diskVector.firstElement().iNodeIDVector.size(); i++)
        {
            System.out.println(Global.getINODE(sb.diskVector.elementAt(i).iNodeIDVector.get(i)).startPoint + " " + Global.getINODE(sb.diskVector.elementAt(i).iNodeIDVector.get(i)).size);        
        }
                
        //5. Read
        int[] buffer;
        
    	Scanner scan = new Scanner(System.in);    	
    	int index; // 시작점
    	int size; // 크기
      	System.out.println("Enter the index and size :");
    	
    	index = scan.nextInt();
    	size = scan.nextInt();
    	
    	buffer = new int[size];
    	
       	System.out.println("Index and size values are entered.");
    	
    	for (int i = 0; i < sb.diskVector.firstElement().iNodeIDVector.size(); i++) //조건 맞는 Disk inodeIDVector에서 찾는 루프
        {
    		//입력받은 인덱스 값이 StartPoint 와 SP+ Size 사이일 경우
    		if(Global.getINODE(sb.diskVector.firstElement().iNodeIDVector.get(i)).startPoint + Global.getINODE(sb.diskVector.firstElement().iNodeIDVector.get(i)).size >= index && Global.getINODE(sb.diskVector.firstElement().iNodeIDVector.get(i)).startPoint <=index)
    		{
    			// 입력받은 인덱스 + 크기 <= StartPoint + Size
	    		if(Global.getINODE(sb.diskVector.firstElement().iNodeIDVector.get(i)).startPoint + Global.getINODE(sb.diskVector.firstElement().iNodeIDVector.get(i)).size >= index + size)
	    		{	    			
	    			buffer = sb.diskVector.firstElement().readDisk(index, size);
	    			//return 받은 결과물 출력
	    			for(int k=0; k < buffer.length; k++)
	    	    	{
	    	    		System.out.println(buffer[k] + " ");
	    	    	}
	    			break;
	    		}
	    		else
	    		{
	    			System.out.println("Error : Out of range.");
	    			break;
	    		}
    		}
    		else
    		{
    			System.out.println("Error : Index out of range");
    		}
        }

        */

        Console c = new Console(new SuperBlock());
        c.start();
    }
}

