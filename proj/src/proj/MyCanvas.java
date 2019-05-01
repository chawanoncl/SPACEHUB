/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proj;


import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.logging.Logger;


public class MyCanvas extends Canvas implements Runnable{
	ObjectPool objectpool;
	KeyInput keyinput;
	Image imgBuf;
	Graphics gBuf;
	Random random;
	Title title;
	Score score;
	
	
	public int scene;
	static final int SCENE_TITLE = 0;
	
        static final int SCENE_GAMEMAIN = 1;
	
	public boolean paused;
	public boolean gameover;
	int counter;
	
	
	
	static final int SHOT_PRESSED = 1;
	
	static final int SHOT_DOWN = 2;
        
	int shotkey_state;
        int pausekey_state;
        static final int PAUSE_PRESSED = 1;
	
	static final int PAUSE_DOWN = 2;
	private final Object lock = new Object();
	MyCanvas()
	{
		
		keyinput = new KeyInput();  //เช็คว่ากดอะไร
		addKeyListener(keyinput);
		setFocusable(true);				
		random = new Random();			
		title = new Title();
		score = new Score();
	}

	
	public void init()
	{
		objectpool = new ObjectPool();
		Score.loadScore();
		
		
		scene = SCENE_TITLE;
		gameover = false;
		
		Level.initLevel();
		
		Score.initScore();
	}
	
	
	public void initThread()
	{
		Thread thread = new Thread(this);
		thread.start();
	}
	
	
	public void paint(Graphics g)
	{
		
		g.drawImage(imgBuf, 0, 0, this);
	}

	
	public void run()
	{
		
		imgBuf = createImage(600, 600);  // พื้นหลัง
		gBuf = imgBuf.getGraphics();
		
		for(counter = 0; ; counter++)
		{
			shotkey_state = keyinput.checkShotKey();
                        pausekey_state = keyinput.checkESCKey();
			
			gBuf.setColor(Color.white);
			gBuf.fillRect(0, 0, 600, 600);

			
			switch (scene)
			{
				
				case 0: //หน้าแรก
					title.drawTitle(gBuf); 
					
					score.drawHiScore(gBuf);
					
					
					if (shotkey_state == SHOT_DOWN)
					{
						
						scene = SCENE_GAMEMAIN;
					}
					break;
				
				
				case 1:
                                        
					
                                        if (pausekey_state == PAUSE_DOWN)
					{
						
						pause();
                                                  if (shotkey_state == SHOT_DOWN)
					{
						
						 
					}
					}
                                       
                                        else 
                                                gameMain();
					break;
			}

			
			repaint();
			
			try{
				Thread.sleep(20);			
			}
			catch(InterruptedException e)
			{}
		}
	}
        
     
   

	
	public void update(Graphics g)
	{
		paint(g);
	}
        
       public void pause()
       {
           synchronized (lock) {
         try {
                        lock.wait(); 
                    } catch (InterruptedException ex) {
       }
       }
       }
       
       
       
	 public void resume()
       {
           synchronized (lock) {
        lock.notifyAll();
       }
       }
        
        
	void gameMain()
	{
            
		if (objectpool.isGameover())
		{
			
			title.drawGameover(gBuf);
			if (shotkey_state == SHOT_DOWN)
			{
				
				Score.compareScore();
				init();
                                
			}
                }       
                
		  
		
		objectpool.getColision();
		objectpool.movePlayer(keyinput);
		
		
		if (counter % (100 - Level.getLevel() * 10) == 0)
		{
			ObjectPool.newEnemy(100 + random.nextInt(300), 0);
		}

		
		if ((counter % 500) == 0)
		{
			Level.addLevel();
		}
		
		
		if ((shotkey_state == SHOT_PRESSED)&&(counter % 3 == 0))
		{
			objectpool.shotPlayer();
		}

		
		objectpool.drawAll(gBuf);
		
		score.drawScore(gBuf);
		score.drawHiScore(gBuf);
		
                
                
	}
}
        
        



