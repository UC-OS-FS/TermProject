package TermProject;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.Scanner;

/*
1. 문현균
2. 박민근
3. 박민근
4. 문현균
5. 손진우
6. 유승화
7. 유승화R
8. 손진우
9. 이인홍
10. 이인홍
 */

class SuperBlock {
    Vector<Disk> diskVector;
    SuperBlock() {
        diskVector = new Vector<>();
    }
}

public class Disk {
    HashMap<Integer, Integer> fileMap;  //index, size
    int[][] blocks;

    //1. Initialize
    Disk(int numBlocks, int blockSize) {
        fileMap = new HashMap<>();
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
        int total = getNumBlocks()*getBlockSize();

        for(Map.Entry<Integer, Integer> elem : fileMap.entrySet()){
            total -= elem.getValue().intValue();
        }

        return total;
    }
    //5. Read space
    //col = getnumBlock
    boolean readDisk(Disk disk)
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
    		return false;
    	}
    	else
    	{    		
    		for(int i =0; i < size; i++)
    		{
    			System.out.print(tempBlock[(i+index)/col][(i+index)%col]);
    		}
    		scan.close();
    		return true;
    	}
    }

    public static void main(String[] args) {
        SuperBlock sb = new SuperBlock();
        Disk disk = new Disk(3, 4);
        sb.diskVector.add(disk);
    }
}
