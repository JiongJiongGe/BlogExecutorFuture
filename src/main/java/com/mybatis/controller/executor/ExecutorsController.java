package com.mybatis.controller.executor;

import com.mybatis.domain.pen.CalmWangPenModel;
import com.mybatis.domain.user.CalmWangUserModel;
import com.mybatis.service.pen.CalmWangPenServiceI;
import com.mybatis.service.user.CalmWangUserServiceI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 线程
 *
 * Created by yunkai on 2017/7/17.
 */
@RequestMapping("executor")
@RestController
public class ExecutorsController {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorsController.class);

    @Autowired
    private CalmWangUserServiceI miaoGeUserService;

    @Autowired
    private CalmWangPenServiceI miaoGePenService;

    @GetMapping(value = "task")
    public String task(){
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Future<CalmWangUserModel>> resultList = new ArrayList<Future<CalmWangUserModel>>();

        //创建10个任务并执行(可以才用for循环)
        //使用ExecutorService执行Callable类型的任务，并将结果保存在future变量中
        Future<CalmWangUserModel> future = executorService.submit(new TaskWithResult(1));
        //将任务执行结果存储到List中
        resultList.add(future);
        //获取pen表数据,看看非阻塞状态
        List<CalmWangPenModel> pens = miaoGePenService.findAll();
        logger.info("pens============" + pens);
        //遍历任务的结果
        for (Future<CalmWangUserModel> fs : resultList){
            try{
                while(!fs.isDone());//Future返回如果没有完成，则一直循环等待，直到Future返回完成
                CalmWangUserModel user = fs.get();
                logger.info("phone===========" + user.getUser_phone());
            }catch(InterruptedException e){
                e.printStackTrace();
            }catch(ExecutionException e){
                e.printStackTrace();
            }finally{
                //启动一次顺序关闭，执行以前提交的任务，但不接受新任务
                executorService.shutdown();
            }
        }
        return "task success";
    }

    class TaskWithResult implements Callable<CalmWangUserModel> {
        private int id;
        public TaskWithResult(int id){
            this.id = id;
        }
        /**
         * 任务的具体过程，一旦任务传给ExecutorService的submit方法，
         * 则该方法自动在一个线程上执行
         */
        public CalmWangUserModel call() throws Exception {
            //该返回结果将被Future的get方法得到
            for(int j = 0; j < 2000; j++){
               logger.info("length========" + miaoGePenService.findAll().size());
            }
            logger.info("call()方法被自动调用！！！    " + Thread.currentThread().getName() + "i===========" + id);
            CalmWangUserModel user = new CalmWangUserModel();
            user.setUser_name("wjd");
            user.setUser_phone("13588313834");
            return user;
        }
    }
}


