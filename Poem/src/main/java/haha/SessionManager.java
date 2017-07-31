package haha;

import org.apache.log4j.Logger;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 会话管理器<br/>
 * 负责会话的创建和删除
 * 自定义缓存机制
 */
public class SessionManager {
static class EnqueueEvent {
   Session session;
   long time;

   EnqueueEvent(Session session, long time) {
      this.session = session;
      this.time = time;
   }

   @Override
   public String toString() {
      return String.format("user=%s,time=%d,last_access_time=%d", session.openid, time, session.lastAccessTime);
   }
}

static ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();
static BlockingDeque<Session> loop = new LinkedBlockingDeque<>();//session队列
static BlockingDeque<EnqueueEvent> active = new LinkedBlockingDeque<>();//session队列
static Logger logger = Logger.getLogger(SessionManager.class);
static final long sessionActiveLength = 60 * 1000;

//获取session
public static Session openSession(String openid) {
   Session session = sessionMap.get(openid);
   if (session == null) {
      session = new Session(openid);
      sessionMap.put(openid, session);
   }
   session.lastAccessTime = System.currentTimeMillis();
   active.add(new EnqueueEvent(session, session.lastAccessTime));
   return session;
}

//定制消息发送任务
public static void submit(String openid, Runnable runnable) {
   Session session = openSession(openid);
   session.tosay.add(runnable);
   loop.add(session);
}

public static void listenDeleteSession() {
   System.out.println("listenting delete session");
   while (true) {
      try {
         EnqueueEvent e = active.take();
         if (e.session.lastAccessTime > e.time) {//如果不是最新事件了
            continue;
         } else {//必然等于：e.time=session.lastAccessTime
            if (e.time + sessionActiveLength < System.currentTimeMillis()) {//如果已经过时了
               sessionMap.remove(e.session.openid);
            } else {
               Thread.sleep(e.time + sessionActiveLength - System.currentTimeMillis());
               //睡醒之后需要重新检测一下
               //e.time=e.session.lastAccessTime表示此事件为最新事件，即最后一次访问
               if (e.time == e.session.lastAccessTime && e.time + sessionActiveLength <= System.currentTimeMillis()) {//如果已经过时了
                  sessionMap.remove(e.session.openid);
               }
            }
         }
      } catch (Exception e) {
         logger.error("", e);
      }
   }
}

//处理后台运行的发送消息任务
public static void listenBlockingQueue() {
   while (true) {
      try {
         Session session = null;
         session = loop.take();
         session.tosay.forEach(x -> {
            x.run();
         });
         session.tosay.clear();
      } catch (InterruptedException e) {
         logger.error("", e);
      }
   }
}
}
