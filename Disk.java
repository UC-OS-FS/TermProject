//package TermProject;

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

    //4. Empty space
    int getFreespace() {
        return getNumBlocks()*getBlockSize()
                - iNodeIDVector.stream().map(i -> Global.getINODE(i).size).mapToInt(i -> i).sum();
    }
    
    //5. Read disk
    int[] readDisk(int index, int size)
    {
    	int[] buffer;
    	buffer = new int[size];
    	
    	for (int i = 0; i < this.iNodeIDVector.size(); i++) //조건 맞는 Disk inodeIDVector에서 찾는 루프
    	{
	    	//입력받은 인덱스 값이 StartPoint 와 SP+ Size 사이일 경우
			if(Global.getINODE(this.iNodeIDVector.get(i)).startPoint + Global.getINODE(this.iNodeIDVector.get(i)).size >= index && Global.getINODE(this.iNodeIDVector.get(i)).startPoint <=index)
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
	    		else
	    		{
	    			System.out.println("Error : Out of range.");
	    		}
			}
			else
			{
				System.out.println("Error : Index out of range");
			}
    	}
    	return null;
    }
    
  
    

    public static void main(String[] args)
    {
        SuperBlock sb = new SuperBlock(); // 슈퍼블록 할당

        // 디스크 생성
        Disk disk1 = new Disk(3, 4); // NumBlock * BlockSize
       Disk disk2 = new Disk(4, 5); // 
        
        // diskVector에 disk 1, 2 삽입
        sb.diskVector.add(disk1);        
        sb.diskVector.elementAt(0).temp_initial(); // 값 확인 위한 임시값 대입
        
        sb.diskVector.add(disk2);
        sb.diskVector.elementAt(1).temp_initial(); // 값 확인 위한 임시값 대입
        
        // 새 iNode 생성
        INODE iNode1 = new INODE();
        INODE iNode2 = new INODE();
        
        // iNode 삽입
        sb.diskVector.elementAt(0).iNodeIDVector.add(iNode1.id);        
        sb.diskVector.elementAt(1).iNodeIDVector.add(iNode2.id);
        
        // create 대체
        
        //파일1 에 대한 inode1
        iNode1.startPoint = 3;
        iNode1.size = 9;
      
        //파일2 에 대한 inode2
        iNode2.startPoint = 6;
        iNode2.size = 3;       
        		
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
       /* int[] buffer;
        
    	Scanner scan = new Scanner(System.in);    	
       	int disk_index; // 디스크 인덱스       	
    	int index; // 시작점
    	int size; // 크기
    
    	System.out.println("Enter the disk index you want to read : ");
    	disk_index = scan.nextInt();
       	
      	System.out.println("Enter the index and size :");
    	index = scan.nextInt();
    	size = scan.nextInt();
    	
    	buffer = new int[size];
    	
       	System.out.println("Index and size values are entered.");
    	
       	buffer = sb.diskVector.elementAt(disk_index).readDisk(index, size);
     	//return 받은 결과물 출력
       	if(buffer != null)
       	{
			for(int k=0; k < buffer.length; k++)
	    	{
	    		System.out.println(buffer[k] + " ");
	    	}
       	}
       	scan.close();*/
       	
      /*  System.out.println(Thread.currentThread().getName());
        disk1.start();
      	disk1.start();*/
        
      	System.out.println(Thread.currentThread().getName());
      	for(int i=0; i<3; i++)
      	{
	      	new Thread("" + i)
	      	{
		      	public void run()
		      	{
		      		 int[] buffer;
		            
		        	Scanner scan = new Scanner(System.in);    	
		           	int disk_index; // 디스크 인덱스       	
		        	int index; // 시작점
		        	int size; // 크기
		        
		        	disk_index = 0;
		            index = 3;
		        	size = 5;
		        	
		        	buffer = new int[size];
		        	
		           	System.out.println("Index and size values are entered.");
		        	
		           	buffer = sb.diskVector.elementAt(disk_index).readDisk(index, size);
		         	
		           	//return 받은 결과물 출력
		           	if(buffer != null)
		           	{
		           		
		    			for(int k=0; k < buffer.length; k++)
		    	    	{
		    	    		System.out.println("Thread " + getName() + " value : " + buffer[k] + " ");
		    	    	}
		           	}
		           	scan.close();
		      	}
	      	}.start();
      	}

    }
}
