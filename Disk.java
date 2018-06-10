package TermProject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Scanner;


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

    //4. Empty space
    int getFreespace() {
        int total = getNumBlocks()*getBlockSize();

        for(Map.Entry<Integer, Integer> elem : fileMap.entrySet()){
            total -= elem.getValue().intValue();
        }

        return total;
    }
    
    void Delete(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the StartPoint:");
        int StartPoint;
        StartPoint = scan.nextInt();
        fileMap.remove(StartPoint);
        
    }
    
    void Defragment(){
        Set keys = fileMap.keySet();
        List <Integer>list = new ArrayList(keys);
        Collections.sort(list);
        int len = list.size();
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
            
    }

    public static void main(String[] args) {
        SuperBlock sb = new SuperBlock();
        Disk disk = new Disk(3, 4);
        sb.diskVector.add(disk);
    }
}
