package model.Algos;

import model.Process;

import java.util.Collections;
import java.util.LinkedList;


public class BuddyAlgo implements Algo {
    private LinkedList<Block> memory;
    private String name;
    public BuddyAlgo(int memSize){
        memory=new LinkedList<>();
        memory.add(new Block(0,memSize));
        name = "Buddy";
    }
    @Override
    public Long allocPs(Process unallocated) {
        for(Block b: memory){
            if(b.getLength()>unallocated.getSize()){
                long base=b.getBase();
                b.setBase(b.getBase()+unallocated.getSize());
                b.setLength(b.getLength()-unallocated.getSize());
                unallocated.setBaseAddress(base);
                return new Long(base);
            }
        }
        return null;
    }

    @Override
    public boolean deallocate(Process allocated) {
        Block b=new Block(allocated.getBaseAddress().get(),allocated.getSize());
        merge(b);
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    private void merge(Block newBlock){
        for(Block b:memory){
            if(b.getBase()==newBlock.getBase()+newBlock.getLength()){
                b.setBase(newBlock.getBase());
                return;
            }
            else if(b.getBase()+b.getLength()==newBlock.getBase()){
                b.setLength(b.getLength()+newBlock.getLength());
                return;
            }
        }
        memory.add(newBlock);
        Collections.sort(memory);
    }
    private class Block implements Comparable{
        long base;
        long length;

        public Block(long base, long length) {
            this.base = base;
            this.length = length;
        }

        public long getBase() {
            return base;
        }

        public void setBase(long base) {
            this.base = base;
        }

        public long getLength() {
            return length;
        }

        public void setLength(long length) {
            this.length = length;
        }

        @Override
        public int compareTo(Object o) {
            if(!(o instanceof Block)){
                return 0;
            }
            Block newBlock=(Block)o;
            if(newBlock.getBase()==getBase()){
                return 0;
            }
            if(newBlock.getBase()>getBase()){
                return -1;
            }
            return 1;
        }
    }
}
