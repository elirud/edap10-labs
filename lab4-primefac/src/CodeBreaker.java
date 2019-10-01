

import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import client.view.ProgressItem;
import client.view.StatusWindow;
import client.view.WorklistItem;
import network.Sniffer;
import network.SnifferCallback;
import rsa.Factorizer;
import rsa.ProgressTracker;

public class CodeBreaker implements SnifferCallback {

    private final JPanel workList;
    private final JPanel progressList;
    
    private final JProgressBar mainProgressBar;
    
    private ExecutorService pool = Executors.newFixedThreadPool(2);

    // -----------------------------------------------------------------------
    
    private CodeBreaker() {
        StatusWindow w  = new StatusWindow();

        workList        = w.getWorkList();
        progressList    = w.getProgressList();
        mainProgressBar = w.getProgressBar();
        
        w.enableErrorChecks();
        
        new Sniffer(this).start();
    }
    
    // -----------------------------------------------------------------------
    
    public static void main(String[] args) throws Exception {

        /*
         * Most Swing operations (such as creating view elements) must be
         * performed in the Swing EDT (Event Dispatch Thread).
         * 
         * That's what SwingUtilities.invokeLater is for.
         */

        SwingUtilities.invokeLater(() -> new CodeBreaker());
    }

    // -----------------------------------------------------------------------

    /** Called by a Sniffer thread when an encrypted message is obtained. */
    @Override
    public void onMessageIntercepted(String message, BigInteger n) {
    	
    	SwingUtilities.invokeLater(() -> {
        	
            System.out.println("message intercepted (N=" + n + ")...");
            
            JButton b = new JButton("Break");
            JButton b2 = new JButton("Remove");
            
            
            WorklistItem wli = new WorklistItem(n, message);
            ProgressItem pi = new ProgressItem(n, message);
            wli.add(b);
            b2.addActionListener(e -> {
            	
            	progressList.remove(pi);
            	mainProgressBar.setValue(mainProgressBar.getValue() - 1000000);
            	mainProgressBar.setMaximum(mainProgressBar.getMaximum() - 1000000);
            	
            });
            
            b.addActionListener(e -> {
            	
            	SwingUtilities.invokeLater(() -> workList.remove(wli));
            	SwingUtilities.invokeLater(() -> progressList.add(pi));
            	SwingUtilities.invokeLater(() -> b.getParent().remove(b));
            	mainProgressBar.setMaximum(mainProgressBar.getMaximum() + 1000000);
            	pool.submit(() -> {
            		
            		String plainText = Factorizer.crack(message, n, new Tracker(pi));
            		SwingUtilities.invokeLater(() -> pi.getTextArea().setText(plainText));
                    pi.add(b2);
            		
            	});
            	
            });
            
            workList.add(wli);            
    		
    	});
    	
    }
    
    
    private class Tracker implements ProgressTracker {
        private int totalProgress = 0;
        ProgressItem progressItem;
        
        public Tracker(ProgressItem progressItem) {
        	
        	this.progressItem = progressItem;
        	
        }

        /**
         * Called by Factorizer to indicate progress. The total sum of
         * ppmDelta from all calls will add upp to 1000000 (one million).
         * 
         * @param  ppmDelta   portion of work done since last call,
         *                    measured in ppm (parts per million)
         */
        @Override
        public void onProgress(int ppmDelta) {
        	
        	totalProgress += ppmDelta;
        	SwingUtilities.invokeLater(() -> {
        		
                progressItem.getProgressBar().setValue(totalProgress);
            	mainProgressBar.setValue(mainProgressBar.getValue() + ppmDelta);      
        		
        	});

            
        }
    }    
}
