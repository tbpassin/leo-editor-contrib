//@+leo-ver=4-thin
//@+node:zorcanda!.20051108103717.132:@thin FreeLeoMind.java
//@@language java

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.OverlayLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;

import freemind.controller.Controller;
import freemind.controller.MenuBar;
import freemind.controller.NodeDragListener;
import freemind.controller.NodeDropListener;
import freemind.extensions.HookFactory;
import freemind.modes.ModeController;
import freemind.preferences.FreemindPropertyListener;
import freemind.view.mindmapview.MapView;
import freemind.main.*;
import org.leo.ImageJPanel;

public class FreeLeoMind implements FreeMindMain {

    public static final String RESOURCE_LOOKANDFEEL = "lookandfeel";
    public static final String RESOURCE_ANTIALIAS = "antialias";
    public static final String RESOURCE_LANGUAGE = "language";
    public static final String RESOURCES_SELECTION_METHOD = "selection_method";
    public static final String RESOURCES_NODE_STYLE = "standardnodestyle";
    public static final String RESOURCES_ROOT_NODE_STYLE = "standardrootnodestyle";
    public static final String RESOURCES_NODE_COLOR = "standardnodecolor";
    public static final String RESOURCES_SELECTED_NODE_COLOR = "standardselectednodecolor";
    public static final String RESOURCES_EDGE_COLOR = "standardedgecolor";
    public static final String RESOURCES_EDGE_STYLE = "standardedgestyle";
    public static final String RESOURCES_CLOUD_COLOR = "standardcloudcolor";
    public static final String RESOURCES_LINK_COLOR = "standardlinkcolor";
    public static final String RESOURCES_BACKGROUND_COLOR = "standardbackgroundcolor";
    
    
    private static Logger logger =null;
    
    private static final String DEFAULT_LANGUAGE = "en";
    private HookFactory nodeHookFactory;
	public static final String version = "0.8.0 RC4";
    //    public static final String defaultPropsURL = "freemind.properties";
    public URL defaultPropsURL;
    //    public static Properties defaultProps;
    public static Properties props; // = new Properties();
    private JScrollPane scrollPane;
    private MenuBar menuBar;
    private JLabel status;
    private Map filetypes;   //Hopefully obsolete. Used to store applications used to open different file types
    private File autoPropertiesFile;
    private File patternsFile;
    private JFrame _frame;
    private JPanel _cpane;
    private JPanel _center;
    private JLayeredPane jlp;
    JMenu _main_menu;
    //private JFrame _cmdframe;
    Controller c;//the one and only controller
    
    private JPanel southPanel; 
    private static PropertyResourceBundle languageResources;

    private PropertyResourceBundle defaultResources;
    
    //@    @+others
    //@+node:zorcanda!.20051108103717.133:FreeLeoMind
    public FreeLeoMind( JFrame frame ) {
    
        //super("FreeMind");
        //    if(logger == null) {
        //        logger = getLogger(FreeMind.class.getName());
        //    }
        //    FreeMindSplash splash = new FreeMindSplash(this);
        //    splash.setVisible(true);
            /* This is only for apple but does not harm for the others. */
        _frame = frame;
        _cpane = new ImageJPanel();
        scrollPane = new JScrollPane();
        System.setProperty("apple.laf.useScreenMenuBar", "true");			
    	String propsLoc = "freemind.properties";
    	defaultPropsURL =  getClass().getClassLoader().getResource( propsLoc );//ClassLoader.getSystemResource(propsLoc);
        
    
    	//load properties
    
    	//Default Properties from .jar file
    	Properties def = new Properties();
    	try {
    	    InputStream in = defaultPropsURL.openStream();
    	    try {
    		def.load(in);
    	    } catch (Exception ex) {
    		System.err.println("Panic! Error while loading default properties.");
    	    } finally {
    		in.close();
    	    }
            
            props = def;
    
    	    //Users Properties, with highest priority (overwrite both other)
    	    Properties user = new Properties(def);
                File userPropertiesFolder = new File (getFreemindDirectory());
                File userProperties = new File (userPropertiesFolder,def.getProperty("userproperties"));
                autoPropertiesFile = new File (userPropertiesFolder,def.getProperty("autoproperties"));
                patternsFile = new File (userPropertiesFolder,def.getProperty("patternsfile"));
    	    try {
                		// move freemind to .freemind:
    				if (getProperty("properties_folder").startsWith(".")) {
    					String oldFolderName = System.getProperty("user.home")
    							+ System.getProperty("file.separator")
    							+ getProperty("properties_folder").substring(1);
    					File oldFolder = new File(oldFolderName);
    					if (oldFolder.exists() && !userPropertiesFolder.exists()) {
    						System.out
    								.println("Try to move the properties folder to .properties folder.");
    						oldFolder.renameTo(userPropertiesFolder);
    					}
    				}
                    if (!userPropertiesFolder.exists()) {
    					userPropertiesFolder.mkdir();
    				}
    
                    System.out.println();
                    System.out.println("Looking for user properties:");
                    System.out.println(userProperties);
                    System.out.println();
    
                    if (userProperties.exists()) {
                       in = new FileInputStream(userProperties);
                       user.load(in);
                       System.out.println("User properties found."); }
                    else {
                        String output;
                        System.out.println("User properties not found. It will be automatically created."); 
                        // create user properties preceeded with hashes '#'
                        output = new String ("# automatically generated user.properties file.\n# generated by Freemind version " + version + "\n# To change an option, please remove the '#' at the front of the corresponding line and restart freemind. Otherwise, the changes are ignored.\n");
                        String userPropsLoc = "user.properties";
                        URL userPropsURL = ClassLoader.getSystemResource(userPropsLoc);
                        InputStream inProp  = userPropsURL.openStream();
                        try {
                            BufferedReader br = new BufferedReader(new InputStreamReader(inProp));
                            String line;
                            while((line = br.readLine()) != null) {
                                output += "#"+line+"\n";
                            }
                            BufferedWriter fileout = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(userProperties) ) );
                            try {
                                fileout.write(output);
                                System.out.println("Done.");
                                // end creation.
                            } catch (Exception ex) {	 
                                System.err.println(ex);
                            } finally {
                                fileout.close();
                            }
                        } catch (Exception ex) {	 
                            System.err.println(ex);
                        } finally {
                            inProp.close();
                        }
                    }
    	    } catch (Exception ex) {	 
                   System.err.println(ex);
    	    } finally {
    		in.close();
    	    }
    
    	    //Automagical properties, generated by freemind itself
    	    Properties auto = new Properties(user);
    	    try {
    		in = new FileInputStream(autoPropertiesFile);
    		auto.load(in);
    	    } catch (Exception ex) {
    		//		System.err.println("No auto properties loaded.");
    	    } finally {
    		in.close();
    	    }
    
    	    props=auto;//priority of files might be changed again
    
    	} catch (Exception ex) {
    	    //even close() fails. what now?
    	}
    
    	//updateLookAndFeel();
    
    	//ImageIcon icon = new ImageIcon(getResource("images/FreeMindWindowIcon.png"));
    	//setIconImage(icon.getImage());
    
    	//Layout everything
    	getContentPane().setLayout( new BorderLayout() );
        
        
    	c = new Controller2(this);
        // add a listener for the controller, resource bundle:
        Controller.addPropertyChangeListener(new FreemindPropertyListener() {
    
            public void propertyChanged(String propertyName, String newValue,
                    String oldValue) {
                if (propertyName.equals(RESOURCE_LANGUAGE)) {
                    // re-read resources:
                    languageResources = null;
                    getResources();
                }
            }
        });
        //fc, disabled with purpose (see java look and feel styleguides).
        //http://java.sun.com/products/jlf/ed2/book/index.html
    //    // add a listener for the controller, look and feel:
    //    Controller.addPropertyChangeListener(new FreemindPropertyListener() {
    //
    //        public void propertyChanged(String propertyName, String newValue,
    //                String oldValue) {
    //            if (propertyName.equals(RESOURCE_LOOKANDFEEL)) {
    //            	updateLookAndFeel();
    //            }
    //        }
    //    });
    
    
        c.optionAntialiasAction.changeAntialias(getProperty(RESOURCE_ANTIALIAS));
    
    	//Create the MenuBar
    	menuBar = new MenuBar(c);
        menuBar.updateMenus();
    	//frame.setJMenuBar(menuBar);
        _main_menu = new JMenu( "FreeMind" );
        for( final Component c: menuBar.getComponents() ){
            menuBar.remove( c );
            _main_menu.add( c );
            
            }
            //Create the scroll pane
            
            // set the default size (PN)
            int win_width  = Integer.parseInt(props.getProperty("appwindow_width","0"));
            int win_height = Integer.parseInt(props.getProperty("appwindow_height","0"));
            win_width  = (win_width  > 0) ? win_width  : 640;
            win_height = (win_height > 0) ? win_height : 440;
            //_frame.getRootPane().setPreferredSize(new Dimension( win_width, win_height ));
        
        //_center = new JPanel();
        //OverlayLayout oll = new OverlayLayout( _center );
        //_center.setLayout( oll );
        //_center.add( scrollPane );
    	///setContentPane().add( _center, BorderLayout.CENTER );
        getContentPane().add( scrollPane, BorderLayout.CENTER );
    
    //	status = new JLabel();
    //	getContentPane().add( status, BorderLayout.SOUTH );
    	// taken from Lukasz Pekacki, NodeText version:
    	southPanel = new JPanel(new BorderLayout());
    	
    	
    	status = new JLabel();
    	southPanel.add( status, BorderLayout.SOUTH );
    	
    	getContentPane().add( southPanel, BorderLayout.SOUTH );
    	// end taken.
    	
    	//Disable the default close button, instead use windowListener
    	//setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    
    	//addWindowListener(new WindowAdapter() {
        //          public void windowClosing(WindowEvent e) {
        //             c.quit.actionPerformed(new ActionEvent(this,0,"quit"));
        //          }		
        //          public void windowActivated(WindowEvent e) {
        //             //This doesn't work the first time, it's called too early to get Focus
        //             if ( (getView() != null) && (getView().getSelected() != null) ) {
        //                getView().getSelected().requestFocus();
        //             }
        //          }
        //    });  
    
            //SwingUtilities.updateComponentTreeUI( _frame ); // Propagate LookAndFeel to JComponents
    
    	c.changeToMode(getProperty("initial_mode"));
        //splash.setVisible(false);
        System.out.println( "AT END OF CONSTRUCTOR!!!!" );
    
        }//Constructor
    //@nonl
    //@-node:zorcanda!.20051108103717.133:FreeLeoMind
    //@+node:zorcanda!.20051108103717.134:getContentPane
    public JPanel getContentPane(){
    
        
        return _cpane;
    
    
    }
    //@nonl
    //@-node:zorcanda!.20051108103717.134:getContentPane
    //@+node:zorcanda!.20051108103717.135:setTitle
    public void setTitle( String title ){
    
    
    
    }
    
    
    
    public void repaint(){}
    //@nonl
    //@-node:zorcanda!.20051108103717.135:setTitle
    //@+node:zorcanda!.20051108103717.136:getMainMenu
    public final JMenu getMainMenu(){
    
        return _main_menu;
    
    }
    
    public void updateMenus(){
    
        
        menuBar.updateMenus();
    	//frame.setJMenuBar(menuBar);
        //_main_menu = new JMenu( "FreeMind" );
        _main_menu.removeAll();
        for( final Component c: menuBar.getComponents() ){
            menuBar.remove( c );
            JMenu jm = (JMenu)c;
            if( jm.getText().equals( "Modes" ) ) continue;
            if( jm.getText().equals( "Maps" ) ) continue;
            if( jm.getText().equals( "File" ) ) continue;
            _main_menu.add( c );
            
            }
        
    }
    //@nonl
    //@-node:zorcanda!.20051108103717.136:getMainMenu
    //@+node:zorcanda!.20051108103717.137:Controller2
    public static class Controller2 extends Controller{
    
        NodeDragListener _ndl;
        NodeDropListener _ndrl;
        
        
        public Controller2( FreeMindMain frame ){
            
            super( frame );
        
        
        }
    
    
        public NodeDragListener getNodeDragListener(){
        
            if( _ndl == null ) return super.getNodeDragListener();
            return _ndl;
        
        }
        
        public void setNodeDragListener( NodeDragListener ndl ){
        
            _ndl = ndl;
        
        
        }
        
        
        public NodeDropListener getNodeDropListener(){
        
            if( _ndrl == null ) return super.getNodeDropListener();
            return _ndrl;
        
        }
        
        public void setNodeDropListener( NodeDropListener ndl ){
        
        
            _ndrl = ndl;
        
        }
        
    }
    //@nonl
    //@-node:zorcanda!.20051108103717.137:Controller2
    //@+node:zorcanda!.20051108103717.138:getCenter
    
    public JScrollPane getScrollPane(){
    
        return scrollPane;
    
    }
    //@nonl
    //@-node:zorcanda!.20051108103717.138:getCenter
    //@-others


    private void updateLookAndFeel() {
      
        
    }

    public boolean isApplet() {
       return false; }

    public File getPatternsFile() {
       return patternsFile; }

    public Container getViewport() {
	    return scrollPane.getViewport();
    }

    public JLayeredPane getLayeredPane(){

        return _frame.getLayeredPane();
    
    }

    public String getFreemindVersion() {
        return version;
    }

     // maintain this methods to keep the last state/size of the window (PN)
    public int getWinHeight() {
      return _frame.getRootPane().getHeight();
    }
    public int getWinWidth() {
      return _frame.getRootPane().getWidth();
    }
    public int getWinState() {
      return _frame.getExtendedState();
    }

    public URL getResource(String name) {
	    return getClass().getClassLoader().getResource(name);
    }

    public String getProperty(String key) {
        	return props.getProperty(key);
    }

	public int getIntProperty(String key, int defaultValue){
		try{
			return Integer.parseInt(getProperty(key));
		}
		catch(NumberFormatException nfe){
			return defaultValue;
		}
	}

	public Properties getProperties() {
		return props;
	}


    public void setProperty(String key, String value) {
	    props.setProperty(key,value);
    }

    public String getFreemindDirectory() {
        String rv = System.getProperty("freemind.base.dir2")+System.getProperty("file.separator") + getProperty("properties_folder");
        return rv;
    }
    
    public void saveProperties() {
	try {
	    OutputStream out = new FileOutputStream(autoPropertiesFile);
	    //	    auto.store(out,null);//to save as few props as possible.
	    props.store(out,null);
	    out.close();
	} catch (Exception ex) {
	}
    }

    public MapView getView() {
	    return c.getView();
    }

    public Controller getController() {
	    return c;
    }

    public void setView(MapView view) {
        scrollPane.setViewportView(view);
	if(view != null) 
	    view.setAutoscrolls(true);//for some reason this doesn't work
    }

    public MenuBar getFreeMindMenuBar() {
	    return menuBar;
    }

    public void out (String msg) {
	    status.setText(msg);
	//	System.out.println(msg);
    }

    public void err (String msg) {
	    status.setText(msg);	
	//	System.out.println(msg);
    }

    /**
     * Open url in WWW browser. This method hides some differences between operating systems.
     */
    public void openDocument(URL url) throws Exception {
        // build string for default browser:
        String correctedUrl = new String(url.toExternalForm());
         if (url.getProtocol().equals("file")) {
             correctedUrl = correctedUrl.replace('\\','/').replaceAll(" ","%20"); 
             // ^ This is more of a heuristic than a "logical" code
             // and due to a java bug: 
             // http://forum.java.sun.com/thread.jsp?forum=31&thread=363990
         }
        // Originally, this method determined external application, with which the document
        // should be opened. Which application should open which document type was
        // configured in FreeMind properties file. As a result, FreeMind tried to solve the
        // problem (of determining application for a file type), which should better be
        // solved somewhere else. Indeed, on Windows, this problem is perfectly solved by
        // Explorer. On KDE, this problem is solved by Konqueror default browser. In
        // general, most WWW browsers have to solve this problem.

        // As a result, the only thing we do here, is to open URL in WWW browser.

        String osName = System.getProperty("os.name");
        if (osName.substring(0,3).equals("Win")) {
            String propertyString = new String("default_browser_command_windows");
			if (osName.indexOf("9") != -1 || osName.indexOf("Me") != -1) {
				propertyString += "_9x";
			} else {
				propertyString += "_nt";
			}

            String browser_command=new String();
            String command=new String();
            // Here we introduce " around the parameter of explorer
            // command. This is not because of possible spaces in this
            // parameter - it is because of "=" character, which causes
            // problems. My understanding of MSDOS is not so good, but at
            // least I can say, that "=" is used in general for the purpose
            // of variable assignment.
            //String[] call = { browser_command, "\""+url.toString()+"\"" };
            try  {
                // This is working fine on Windows 2000 and NT as well
                // Below is a piece of code showing how to run executables directly
                // without asking. However, we don't want to do that. Explorer will run
                // executable, but ask before it actually runs it.
                //
                // Imagine you download a package of maps containing also nasty
                // executable. Let's say there is a map "index.mm". This map contains a
                // link to that nasty executable, but the name of the link appearing to the
                // user does not indicate at all that clicking the link leads to execution
                // of a programm.  This executable is located on your local computer, so
                // asking before executing remote executable does not solve the
                // problem. You click the link and there you are running evil executable.

                // build string for default browser:
                // ask for property about browser: fc, 26.11.2003.
                Object[] messageArguments = { url.toString() };
                MessageFormat formatter = new MessageFormat(getProperty(propertyString));
                browser_command = formatter.format(messageArguments);
        
                if (url.getProtocol().equals("file")) {
                    command = "rundll32 url.dll,FileProtocolHandler "+Tools.urlGetFile(url);
                } else if (url.toString().startsWith("mailto:")) {
                    command = "rundll32 url.dll,FileProtocolHandler "+url.toString(); 
                } else {
                    command = browser_command; 
                }
                //System.out.println("Starting browser with "+command);
                Runtime.getRuntime().exec(command); 
            }
            catch(IOException x) {
                c.errorMessage("Could not invoke browser.\n\nFreemind excecuted the following statement on a command line:\n\""+command+"\".\n\nYou may look at the user or default property called '"+propertyString+"'.");
                System.err.println("Caught: " + x); 
            }
        } else if (osName.startsWith("Mac OS")) {

            // System.out.println("Opening URL "+urlString);
            String browser_command=new String();
            try {
                // ask for property about browser: fc, 26.11.2003.
                Object[] messageArguments = { correctedUrl, url.toString() };
                MessageFormat formatter = new MessageFormat(getProperty("default_browser_command_mac"));
                browser_command = formatter.format(messageArguments);
                Runtime.getRuntime().exec(browser_command); }
            catch(IOException ex2) {
                c.errorMessage("Could not invoke browser.\n\nFreemind excecuted the following statement on a command line:\n\""+browser_command+"\".\n\nYou may look at the user or default property called 'default_browser_command_mac'.");
                System.err.println("Caught: " + ex2); 
            }
        } else {
            // There is no '"' character around url.toString (compare to Windows code
            // above). Putting '"' around does not work on Linux - instead, the '"'
            // becomes part of URL, which is malformed, as a result.	 

            String browser_command=new String();
            try {
                // ask for property about browser: fc, 26.11.2003.
                Object[] messageArguments = { correctedUrl, url.toString() };
                MessageFormat formatter = new MessageFormat(getProperty("default_browser_command_other_os"));
                browser_command = formatter.format(messageArguments);
                Runtime.getRuntime().exec(browser_command); }
            catch(IOException ex2) {
                c.errorMessage("Could not invoke browser.\n\nFreemind excecuted the following statement on a command line:\n\""+browser_command+"\".\n\nYou may look at the user or default property called 'default_browser_command_other_os'.");
                System.err.println("Caught: " + ex2); 
            }
        }
    }

    private String transpose(String input, char findChar, String replaceString) {
        String res = new String();
        for(int i = 0; i < input.length(); ++i) {
            char d = input.charAt(i);
            if(d == findChar) 
                res += replaceString;
            else
                res += d;
        }
        return res;
    }

    public void setWaitingCursor(boolean waiting) {
       if (waiting) {
          _frame.getRootPane().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          _frame.getRootPane().getGlassPane().setVisible(true); }
       else {
          _frame.getRootPane().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          _frame.getRootPane().getGlassPane().setVisible(false); }}

    private String getProgramForFile(String type) {
	if (filetypes == null) {
	    filetypes = new HashMap();
	    String raw = getProperty("filetypes");
	    if (raw == null  || raw.equals("")) {
		return "";
	    }
	    StringTokenizer tokens = new StringTokenizer(raw, ",");
	    while (tokens.hasMoreTokens()) {
		StringTokenizer pair = new StringTokenizer(tokens.nextToken(),":");
		String key = pair.nextToken().trim().toLowerCase();
		String value = pair.nextToken().trim();
		filetypes.put(key,value);
	    }
	}
	return (String)filetypes.get(type.trim().toLowerCase());
    }

    /** Returns the ResourceBundle with the current language */
    public ResourceBundle getResources() {
        if (languageResources == null) {
            try {
                String lang = getProperty(RESOURCE_LANGUAGE);
                if(lang == null || lang.equals("automatic")) {
                    lang = Locale.getDefault().getLanguage() + "_" +  Locale.getDefault().getCountry();
                    if(getLanguageResources(lang)== null) {
	                    lang = Locale.getDefault().getLanguage();
	                    if(getLanguageResources(lang)== null) {
	                        // default is english.
	                        lang=DEFAULT_LANGUAGE;
	                    }
                    }
                } 
                languageResources = getLanguageResources(lang);
                defaultResources = getLanguageResources(DEFAULT_LANGUAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("Error loading Resources");
                return null;
            }
        }
        return languageResources;
    }

    public String getResourceString(String resource) {
        try {
            return getResources().getString(resource);
        } catch (Exception ex) {
            System.err.println("Warning - resource string not found:"
                    + resource);
            try{
                return defaultResources.getString(resource)+"[translate me]";
            } catch(Exception e) {
	            System.err.println("Warning - resource string not found (even in english):"
	                    + resource);
                return resource;
            }
        }
    }

    
    /**
     * @param lang
     * @return
     * @throws IOException
     */
    private PropertyResourceBundle getLanguageResources(String lang) throws IOException {
        URL systemResource = getClass().getClassLoader().getResource( //ClassLoader.getSystemResource(
                "Resources_" + lang + ".properties");
        if(systemResource == null) {
            return null;
        }
        InputStream in = systemResource.openStream();
        if(in == null) {
            return null;
        }
        PropertyResourceBundle bundle = new PropertyResourceBundle(in);
        in.close();
        return bundle;
    }

    public java.util.logging.Logger getLogger(String forClass) {
        return java.util.logging.Logger.getLogger(forClass);
    }

    
    
    

	/* (non-Javadoc)
	 * @see freemind.main.FreeMindMain#getHookFactory()
	 */
	public HookFactory getHookFactory() {
		if(nodeHookFactory == null) {
			nodeHookFactory = new HookFactory(this);
		}
		return nodeHookFactory;
	}
	/**
	 * @return
	 */
	public JPanel getSouthPanel() {
		return southPanel;
	}

	/* (non-Javadoc)
	 * @see freemind.main.FreeMindMain#getJFrame()
	 */
	public JFrame getJFrame() {
    
		return _frame;
	}
    
    public void setFrame( JFrame frame ){
    
        _frame = frame;
    
    }

}
//@nonl
//@-node:zorcanda!.20051108103717.132:@thin FreeLeoMind.java
//@-leo
