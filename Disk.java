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



    void Delete(Integer iNodeID) {
        iNodeIDVector.remove(iNodeIDVector.indexOf(iNodeID));
        /*
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the DISK and ID(diskid id):");
        int delID;
        int diskID;
        diskID = scan.nextInt();
        delID = scan.nextInt();
        boolean check = false;
        for(int i=0; i<SB.diskVector.size(); i++) {
            if(SB.diskVector.get(i).id == diskID) {
                for(int j=0; j<SB.diskVector.get(i).iNodeIDVector.size(); j++) {
                    if(SB.diskVector.get(i).iNodeIDVector.get(j) == delID) {
                        SB.diskVector.get(i).iNodeIDVector.remove(j);
                        check = true;
                    }
                }                
            }            
        }
        if(check == true) {
            System.out.println("Inode not found");
        }
        */
    }
    
    void Degfragment() {
        /*
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the DISKID :");
        int diskID;
        diskID = scan.nextInt();
        
        for(int i=0; i<SB.diskVector.size(); i++) {
            if(SB.diskVector.get(i).id == diskID) {
                
            }            
        }
        /*
        for(int i=0; i<len; i++){
            if(i == 0){
                if(list.get(i) != 0){
                    int temp;
                    temp = fileMap.get(list.get(i));
                    fileMap.remove(list.get(i));
                    fileMap.put(0, temp);
                }           
               
            }
            else {
                if(list.get(i-1)+fileMap.get(list.get(i-1))+1 != list.get(i)) {
                     int temp;
                    temp = fileMap.get(list.get(i));
                    fileMap.remove(list.get(i));
                    fileMap.put(list.get(i-1)+fileMap.get(list.get(i-1))+1, temp);
                }
            }
            
        }
    */
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

