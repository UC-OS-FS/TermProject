package TermProject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Scanner;

import java.util.Scanner;

import java.lang.Thread;
import java.lang.Runnable;

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

class MyThread extends Thread
{
	public void run(Disk disk) // Function - Parallel Reading
	{
		Scanner scan = new Scanner(System.in);
    	System.out.println("Enter the index and size :");
    	int index; // 시작점
    	int size; // 크기
    	index = scan.nextInt();
    	size = scan.nextInt();
    	
    	int row = disk.getNumBlocks();
    	int col = disk.getBlockSize();
    	
    	int[][] tempBlock = disk.getBlock();

    	if((index + size) -1 > (row*col) -1 ) // 사이즈 확인
    	{
    		System.out.println("Wrong values...");
    		scan.close();    	
    	}
    	else
    	{    		
    		for(int i =0; i < size; i++)
    		{
    			System.out.print(tempBlock[(i+index)/col][(i+index)%col]);
    		}
    		scan.close();
    	}
	}
}

public class Disk {
    public final Integer id;
    Vector<Integer> iNodeIDVector;
    int[][] blocks;
    int numBlocks;
    int blockSize;
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
   int[][] getBlock()
   {
	   return blocks;
   }

    //4. Empty space
    int getFreespace() {
        return getNumBlocks()*getBlockSize()
                - iNodeIDVector.stream().map(i -> Global.getINODE(i).size).mapToInt(i -> i).sum();
    }


    //5. Read space
    void readDisk() {
    	Scanner scan = new Scanner(System.in);
    	System.out.println("Enter the index and size :");
    	int index; // 시작점
    	int size; // 크기
    	index = scan.nextInt();
    	size = scan.nextInt();
    	
    	int row = getNumBlocks();
    	int col = getBlockSize();

    	if((index + size) -1 > (row*col) -1 ) // 사이즈 확인
    	{
    		System.out.println("Wrong values...");
    		scan.close();
    		return;
    	}
    	else
    	{    		
    		for(int i =0; i < size; i++)
    		{
    			System.out.print(blocks[(i+index)/col][(i+index)%col]);
    		}
    		scan.close();
    		return;
    	}
    }

    int findStartPoint(int inputSize){
        Set keys = fileMap.keySet();
        List list = new ArrayList(keys);
        Collections.sort(list);


        int beginning = 0;
        int end = blockSize * numBlocks;


        if (list.toArray().length == 0){
            if(inputSize <= numBlocks * blockSize)
                return 0;
            else
                System.out.println("Too big file size");
            return -1;
        }

        int output = -1;
        int idx = 0;

        do {
            if (idx == 0){
                if (inputSize < (int)list.toArray()[idx] - beginning)
                    //return beginning;

                    output = beginning;
            }

            else if (idx == list.toArray().length -1){
                if(inputSize < end - ((int)list.toArray()[idx] + fileMap.get(list.toArray()[idx])))
                    output = ((int)list.toArray()[idx] + fileMap.get(list.toArray()[idx]));

            }

            else {
                if( inputSize <  (int)list.toArray()[idx] - ((int)list.toArray()[idx-1] + fileMap.get(list.toArray()[idx-1])))
                    output = ((int)list.toArray()[idx-1] + fileMap.get(list.toArray()[idx-1]));
            }
            idx++;
        }while(idx < list.toArray().length);


        return output;
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
            //System.exit(1);
        }

        // from start point, write
        for (int i = startPoint, bufferIdx = 0; i < startPoint + inputSize; i++, bufferIdx++){
            int row = i / blockSize;
            int col = i % blockSize;

            blocks[row][col] = Integer.parseInt(elem[bufferIdx]);
        }

        fileMap.put(startPoint, inputSize);
    }

    void write(int fileStartPoint){

        if (!fileMap.containsKey(fileStartPoint)) {   //없으면
            System.out.println("cannot find startPoint");
            return;
        }

        Scanner scan = new Scanner(System.in);
        String inputs;
        inputs = scan.nextLine();
        String elem[] = inputs.split(" ");  // Inputs as string array
        int inputSize = elem.length;

        Set keys = fileMap.keySet();
        List list = new ArrayList(keys);
        Collections.sort(list);

        int idx = 0;
        for(int i = 0; i < list.toArray().length; i++){
            if (fileStartPoint == (int)list.toArray()[i]){
                idx = i;
                break;
            }
        }

        int space = 0;
        if(idx != list.toArray().length - 1){
            space = fileMap.get((int)list.toArray()[idx + 1]) - fileStartPoint + fileMap.get(fileStartPoint);
        }
        else if (idx == list.toArray().length - 1){
            space = blockSize * numBlocks - fileStartPoint + fileMap.get(fileStartPoint);
        }

        if(space < inputSize){
            System.out.println("cannot write anymore");
        }
        else{
            for (int i = fileStartPoint + fileMap.get(fileStartPoint), bufferIdx = 0; i < fileStartPoint + fileMap.get(fileStartPoint) + inputSize; i++, bufferIdx++){
                int row = i / blockSize;
                int col = i % blockSize;

                blocks[row][col] = Integer.parseInt(elem[bufferIdx]);
            }
            int newSize = fileMap.get(fileStartPoint) + elem.length;
            fileMap.put(fileStartPoint, newSize);
        }

    }

    public static void main(String[] args) {
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
    }

}
