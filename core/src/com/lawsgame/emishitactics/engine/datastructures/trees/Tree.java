package com.lawsgame.emishitactics.engine.datastructures.trees;

import com.badlogic.gdx.utils.Array;

public class Tree<T>  {
    private Node<T> root;

    public Tree(T rootData){
        root = new Node<T>(rootData);
    }


    public static class Node<T> {
        protected T data;
        protected Node<T> parent;
        protected Array<Node<T>>  children;

        public Node(T data){
            this.children = new Array<Node<T>>();
            this.data = data;
            this.parent = null;
        }

        public void addChild(T data){
            if(data != null){
                Node<T> chilfNode = new Node<T>(data);
                chilfNode.parent = this;
                this.children.add(chilfNode);
            }
        }
    }
}
