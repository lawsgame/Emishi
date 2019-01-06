package com.lawsgame.emishitactics.core.helpers;

import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.WaitTask;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.rendering.Renderer;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class TaskTest {

    private Object bundle0;
    private Object bundle1;
    private TestModel testModel0;
    private TestModel testModel1;
    private Renderer renderer0;
    private Renderer renderer1;

    @Before
    public void before(){
        bundle0 = "0";
        bundle1 = "1";
        testModel0 = new TestModel();
        testModel1 = new TestModel();
        renderer0 = new TestRenderer(testModel0);
        renderer1 = new TestRenderer(testModel1);
    }


    @Test
    public void testMerge(){
        // case  WaitTask merging
        WaitTask task0 = new WaitTask(2);
        WaitTask task1 = new WaitTask(1);
        task0.merge(task1);
        assertEquals(task0.getCountDown().getDelay(), 3f);
        assertTrue(task1.isIrrelevant());

        // case 1
        StandardTask task2 = new StandardTask();
        StandardTask task3 = new StandardTask();
        task3.addParallelSubTask(new StandardTask.RendererSubTaskQueue(renderer0, bundle0));
        task2.merge(task3);
        assertTrue(task3.isIrrelevant());
        assertTrue(task2.getNumberOfSubTasks() == 1);

        // case 2
        task2 = new StandardTask(renderer0, bundle0);
        task3 = new StandardTask(renderer0, bundle1);
        task2.merge(task3);
        assertTrue(task3.isIrrelevant());
        assertTrue(task2.getNumberOfSubTasks()== 1);

        // case 3
        task2 = new StandardTask(renderer0, bundle0);
        task3 = new StandardTask(renderer1, bundle1);
        task2.merge(task3);
        assertTrue(task3.isIrrelevant());
        assertTrue(task2.getNumberOfSubTasks() == 2);


        // case of merging failure n*1 : share same renderer
        task2 = new StandardTask();
        task2.addParallelSubTask(new StandardTask.RendererSubTaskQueue(renderer1, bundle0));
        task2.addParallelSubTask(new StandardTask.RendererSubTaskQueue(renderer0, bundle0));
        task3 = new StandardTask(renderer1, bundle1);
        task2.merge(task3);
        assertTrue(!task2.isIrrelevant());
        assertEquals(task2.getNumberOfSubTasks(), 2);
        assertEquals(task3.getNumberOfSubTasks(), 1);


        // case of merging failure n*1 : non RenderSubTaskQueue included preventing both to merge
        task2 = new StandardTask();
        task2.addParallelSubTask(new StandardTask.CommandSubTask(3){
            @Override
            public void run() {

            }
        });
        task2.addParallelSubTask(new StandardTask.RendererSubTaskQueue(renderer0, bundle0));
        task3 = new StandardTask(renderer1, bundle1);
        task2.merge(task3);
        assertTrue(!task2.isIrrelevant());
        assertEquals(task2.getNumberOfSubTasks(), 2);
        assertEquals(task3.getNumberOfSubTasks(), 1);

    }


    public static class TestRenderer extends Renderer<TestModel>{

        public TestRenderer(TestModel model) {
            super(model);
        }

        @Override
        public boolean isExecuting() {
            return false;
        }

        @Override
        public void setVisible(boolean visible) {

        }

        @Override
        public void update(float dt) {

        }

        @Override
        public void getNotification(Observable sender, Object data) {

        }
    }

    public static class TestModel extends Observable{

    }
}
