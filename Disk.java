package TermProject;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

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
    //3. unmount disk
    void unmountDisk(Disk disk){
        diskVector.remove(disk);
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

    //2. format disk
    void  formatDist(){
        fileMap.clear();
    }

    //4. Empty space
    int getFreespace() {
        int total = getNumBlocks()*getBlockSize();

        for(Map.Entry<Integer, Integer> elem : fileMap.entrySet()){
            total -= elem.getValue().intValue();
        }

        return total;
    }

    public static void main(String[] args) {
        SuperBlock sb = new SuperBlock();
        Disk disk1 = new Disk(3, 4);
        Disk disk2 = new Disk(12,24);
        sb.diskVector.add(disk1);
        sb.diskVector.add(disk2);

        sb.unmountDisk(disk1);
        System.out.println(sb.diskVector.size());
    }
}
