package TermProject;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    int numBlocks;
    int blockSize;
    //1. Initialize
    Disk(int _numBlocks, int _blockSize) {
        fileMap = new HashMap<>();
        blocks = new int[_numBlocks][_blockSize];
        numBlocks = _numBlocks;
        blockSize = _blockSize;
    }

    int getNumBlocks() {
        return blocks == null ? 0 : blocks.length;
    }

    int getBlockSize() {
        return getNumBlocks() > 0 ? blocks[0].length : 0;
    }

    //4. Empty space
    int getFreespace() {
        int total = getNumBlocks()*getBlockSize();

        for(Map.Entry<Integer, Integer> elem : fileMap.entrySet()){
            total -= elem.getValue().intValue();
        }

        return total;
    }

    
    int findStartPoint(int inputSize){
        Set keys = fileMap.keySet();
        List list = new ArrayList(keys);
        Collections.sort(list);
        
        
        int beginning = 0;
        int end = blockSize * numBlocks;
        
        
        for (int i = 0; i < list.toArray().length; i ++){
            if (i == 0){
                if (inputSize < (int)list.toArray()[i] - beginning) 
                    //return beginning;
                    return beginning;
                }
            
            else if (i == list.toArray().length -1){
                if(inputSize < end - ((int)list.toArray()[i] + fileMap.get(list.toArray()[i])))
                    return ((int)list.toArray()[i] + fileMap.get(list.toArray()[i]));
                   
            }

            else {
                if( inputSize <  (int)list.toArray()[i] - ((int)list.toArray()[i-1] + fileMap.get(list.toArray()[i-1])))
                    return ((int)list.toArray()[i-1] + fileMap.get(list.toArray()[i-1]));
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
            System.exit(1);
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
        Disk disk = new Disk(3, 4);
        sb.diskVector.add(disk);
    }
}
