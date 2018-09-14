package com.liuyun.log4j;

import com.liuyun.log4j.appender.AsyncAppender;
import com.liuyun.log4j.helpers.AppenderAttachableImpl;
import com.liuyun.log4j.helpers.BoundedFIFO;
import com.liuyun.log4j.spi.LoggingEvent;


/**
 * Obsolete AsyncAppender dispatcher provided for compatibility only.
 *
 * @deprecated Since 1.3.
 */
class Dispatcher extends Thread {
    /**
     * @deprecated
     */
  private BoundedFIFO bf;
  private AppenderAttachableImpl aai;
  private boolean interrupted = false;
  AsyncAppender container;

    /**
     *
     * @param bf
     * @param container
     * @deprecated
     */
  Dispatcher(BoundedFIFO bf, AsyncAppender container) {
    this.bf = bf;
    this.container = container;
    this.aai = container.aai;

    // It is the user's responsibility to close appenders before
    // exiting.
    this.setDaemon(true);

    // set the dispatcher priority to lowest possible value
    this.setPriority(Thread.MIN_PRIORITY);
    this.setName("Dispatcher-" + getName());

    // set the dispatcher priority to MIN_PRIORITY plus or minus 2
    // depending on the direction of MIN to MAX_PRIORITY.
    //+ (Thread.MAX_PRIORITY > Thread.MIN_PRIORITY ? 1 : -1)*2);
  }

  void close() {
    synchronized (bf) {
      interrupted = true;

      // We have a waiting dispacther if and only if bf.length is
      // zero.  In that case, we need to give it a death kiss.
      if (bf.length() == 0) {
        bf.notify();
      }
    }
  }

  /**
   * The dispatching strategy is to wait until there are events in the buffer
   * to process. After having processed an event, we release the monitor
   * (variable bf) so that new events can be placed in the buffer, instead of
   * keeping the monitor and processing the remaining events in the buffer.
   *
   * <p>
   * Other approaches might yield better results.
   * </p>
   */
  public void run() {
    //Category cat = Category.getInstance(Dispatcher.class.getName());
    LoggingEvent event;

    while (true) {
      synchronized (bf) {
        if (bf.length() == 0) {
          // Exit loop if interrupted but only if the the buffer is empty.
          if (interrupted) {
            //cat.info("Exiting.");
            break;
          }

          try {
            //LogLog.debug("Waiting for new event to dispatch.");
            bf.wait();
          } catch (InterruptedException e) {
            break;
          }
        }

        event = bf.get();

        if (bf.wasFull()) {
          //LogLog.debug("Notifying AsyncAppender about freed space.");
          bf.notify();
        }
      }

      // synchronized
      synchronized (container.aai) {
        if ((aai != null) && (event != null)) {
          aai.appendLoopOnAppenders(event);
        }
      }
    }

    // while
    // close and remove all appenders
    aai.removeAllAppenders();
  }
}
