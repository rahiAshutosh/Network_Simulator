package network_simulator;
import java.util.Scanner;
import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.lang.Exception;
import java.io.*;
import java.util.InputMismatchException;
import java.lang.RuntimeException;
import java.io.Reader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;

class Device
{
    String mac_address = "";//stores the mac address of the device
    int switch_port; //TO WHICH PORT OF THE SWITCH IS THIS DEVICE CONNNECTED.
    String ip_address; 
}

class SMTPClient extends Device
{
    String[] buffer; // STORES THE MAILS TO BE SENT YET.
    public SMTPClient(end_device sender)
    {
        for(int i=0 ; i<sender.frame.length ; i++)
        {
            buffer[i] = sender.frame[i];
        }
    }
    public void sendMail(SMTPServer ss, end_device receiver)
    {
        ss.receiveMail(buffer, ss, receiver);
    }
}

class SMTPServer extends Device
{
    String[] pool; //POOL TO STORE THE MAILS ARRIVED IN BUFFER UNTIL USER LOADS THEM.
    public void receiveMail(String[] arr, SMTPServer ss, end_device receiver)
    {
        for(int i=0;i<arr.length;i++)
        {
            pool[i] = arr[i];
        }    
        receiver.getSMTPServer(ss);
    }
}

class DomainNameServer extends Device
{
    HashMap<String, String> database = new HashMap<String, String>();    
    public DomainNameServer(end_device[] earr)
    {
        for(int i=0; i<earr.length;i++)
        {
            if(earr[i].isServer == true)
            {
                earr[i].domain_name = "www.server.com";
                database.put(earr[i].domain_name, earr[i].ip_address);
            }
        }
    }
    
    String DNSquery(String domain_name) // RETURNS THE IP ADDRESS CORRESPONDING TO THE SOUGHT DOMAIN NAME.
    {
        if(database.containsKey(domain_name))
        {
            return database.get(domain_name);
        }
        else
        {
            return "ERROR 404"; // RETURNS ERROR MESSAGE IF DOMAIN NAME NOT FOUND IN DATABASE.
        }
    }
}

class Router extends Device
{
    String[][] routing_table;
    String[][] translation_table;
    int num_interfaces;
    int router_id;
    int num_entries; //NO. OF ENTRIES IN ROUTING TABLE.
    String[] interfaces; // Stores IP address of interface correspoding to interface no.
    //Device[] interface_device; // Stores the objects of devices at each interface.
    static Scanner usit = new Scanner(System.in);
    static int rk = 1;
    
    Router(String mac, ArrayList<String> input)
    {
        mac_address = mac;
        router_id = rk++;
        System.out.println("----------------------------------------------------------------------------------------------\n");
        System.out.println("ROUTER CONFIGURATION");
        //System.out.println("KINDLY ENTER NUMBER OF INTERFACES OF THIS ROUTER : ");
        //int n = usit.nextInt();
        int n = Integer.parseInt(input.get(NetworkSimulator.inpin++));        
        //usit.nextLine();
        num_interfaces = n;
        interfaces = new String[n];
        //System.out.println("WOULD YOU LIKE TO OPT FOR STATIC ROUTING OR DYNAMIC? ENTER 0 FOR STATIC & 1 FOR DYNAMIC: ");
        //int choice = usit.nextInt();
        int choice = Integer.parseInt(input.get(NetworkSimulator.inpin++));        
        //usit.nextLine();
        int addn;
        if(choice==0)
        {
            //System.out.println("ENTER NO. OF NEW NETWORKS TO BE CONFIGURED BY ADMIN : ");
            //addn = usit.nextInt();            
            addn = Integer.parseInt(input.get(NetworkSimulator.inpin++));
            //usit.nextLine();
        }
        else
        {
            //System.out.println("ENTER NO. OF NEW NETWORKS TO BE CONFIGURED BY ADMIN : ");
            //addn = usit.nextInt();
            addn = Integer.parseInt(input.get(NetworkSimulator.inpin++));
            //usit.nextLine();
        }
        num_entries = num_interfaces + addn;
        routing_table = new String[num_entries][4];
               
        for(int i=0;i<num_interfaces;i++)
        {
            //System.out.println("KINDLY ENTER THE I.P. ADDRESS OF INTERFACE - " + i + " AND THE N.I.D.: ");
            //String ip = usit.nextLine();
            //String nid = usit.nextLine();            
            String ip = input.get(NetworkSimulator.inpin++);
            String nid = input.get(NetworkSimulator.inpin++);
            interfaces[i] = ip;            
            String subnet_mask = get_subnet_mask(ip);
            String nexthop = "NIL";
            routing_table[i][0] = subnet_mask; //FIRST COLUMN : SUBNET MASK
            routing_table[i][1] = nid; //SECOND COLUMN : NETWORK ADDRESS
            routing_table[i][2] = nexthop; // THIRD COLUMN : NEXT-HOP
            routing_table[i][3] = Integer.toString(i); //FOURTH COLUMN : INTERFACE-ID
        }                        
        if(choice == 0) //CALL THE STATIC ROUTING METHOD.
        {
            static_routing(input);
        }
        else //CALL EITHER RIP OR OSPF METHOD ON THE BASIS OF USER'S CHOICE.
        {
            System.out.println("RIP TO BE CARRIED OUT LATER.");
        }
        
        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.println("ROUTING-TABLE AS OF NOW:");
        show_ip_route(); //PRINTS ROUTING TABLE
    }
    
    void sort_for_longest_mask_matching() //TO BE COMPLETED LATER ON.
    {
        for(int i=0;i<num_entries;i++)
        {
            
        }
    }
    
    void static_routing(ArrayList<String> input) //STATIC ROUTING
    {   
        for(int i=num_interfaces; i<num_entries;i++)
        {
            System.out.println("-------------------------------------------------------------------------------------------------------------------");
            System.out.println("STATIC ROUTING : NOW CONFIGURING NEW UNKNOWN NETWORK : ");
            //System.out.println("ENTER THE N.I.D.:");
            //String nid = usit.nextLine();        
            String nid = input.get(NetworkSimulator.inpin++);
            //System.out.println("ENTER THE SUBNET MASK : ");
            //String subnet_mask = usit.nextLine();
            String subnet_mask = input.get(NetworkSimulator.inpin++);
            //System.out.println("ENTER THE NEXT-HOP IP : ");
            //String next_hop = usit.nextLine();
            String next_hop = input.get(NetworkSimulator.inpin++);
            //System.out.println("ENTER THE INTERFACE ID : ");
            //int intid = usit.nextInt();
            //usit.nextLine();
            int intid = Integer.parseInt(input.get(NetworkSimulator.inpin++));
            String interface_id = Integer.toString(intid);
            routing_table[i][0] = subnet_mask;
            routing_table[i][1] = nid;
            routing_table[i][2] = next_hop;
            routing_table[i][3] = interface_id;
        }
        
    }
    
    void acceptRIPinfo(String[][] neighbor_routing_table, Router neighbor, String next_hop, String interfac)
    {
        int k = 0;
        for(int i=0;i<neighbor.num_entries;i++)
        {
            int found = 0;
            for(int j=0 ; j<this.num_entries ; j++)
            {
                if(this.routing_table[j][1].equals(neighbor.routing_table[i][1]))
                {
                    found = 1;
                    break;
                }
            }
            if(found!=1)
            {
                System.out.println("RIP : UPDATING ROUTING TABLE OF ROUTER : " + this.router_id);
                this.routing_table[this.num_entries + k][0] = neighbor.routing_table[i][0]; // SAME SUBNET MASK
                this.routing_table[this.num_entries + k][1] = neighbor.routing_table[i][1]; // SAME NETWORK ADDRESS
                this.routing_table[this.num_entries + k][1] = next_hop;
                this.routing_table[this.num_entries + k][2] = interfac;
                k++;
                System.out.println("UPDATED ROUTING TABLE : ");
                this.show_ip_route();
            }
            
        }
    }
    
    void rip(Router[] r) // CALLED BY A ROUTER TO UPDATE ITS NEIGHBORING ROUTERS.
    {
        int isRouterNext = 0;
        for(int k=0;k<r.length;k++)
        {            
            if(!r[k].equals(this))
            {
                for(int j=0;j<r[k].interfaces.length;j++)
                {
                    for(int i=0; i<this.num_interfaces; i++) //CHECKING ALL INTERFACES OF 'THIS' ROUTER
                    {
                        if(routing_table[i][2].equals(r[k].interfaces[j]))
                        {
                            isRouterNext = 1;
                            System.out.println("ROUTER : " + this.router_id + " REPORT : " + " ROUTING INFORMATION PROTOCOL : SENDING ROUTING TABLE TO ROUTER : " + r[k].router_id);
                            r[k].acceptRIPinfo(this.routing_table, this, routing_table[i][2], routing_table[i][3]);
                            break;
                        }
                    }
                }                
            }
        }        
    }
    
    void show_ip_route() //PRINTS ROUTING TABLE
    {
        System.out.println("-------------------------------------------- ROUTING TABLE -----------------------------------------------------");
        System.out.printf("%20s\t\t|%20s\t\t|%20s\t\t|%20s\n","SUBNET MASK" ,"NETWORK-ADDRESS" ,"NEXT HOP" ,"INTERFACE ID");
        for(int i=0;i<num_entries;i++)
        {            
            System.out.printf("%20s\t\t|%20s\t\t|%20s\t\t|%20s\n", routing_table[i][0],routing_table[i][1] ,routing_table[i][2] , routing_table[i][3]);            
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------");
    }
    
    String returnBinaryIP(String ip) //returns binary of given IP address as A.B.C.D
    {
        String[] ip_part = ip.split("\\.", -2);
        String binary_ip = "";
        for(int i=0;i<4;i++)
        {
            //System.out.println("i=" + i);
            String str = Integer.toString(Integer.parseInt(ip_part[i]),2);
            int strlen = str.length();
            String fr = "";
            if(strlen!=8)
            {
                int numzeros = 8 - strlen;
                int j=0;
                while(j!=numzeros)
                {
                    fr=fr+"0";
                    j++;
                }
                fr=fr+str;                
                binary_ip = binary_ip + fr;
            }
            else
            {
                binary_ip = binary_ip + str;
            }
        }
        return binary_ip;
    }
    
    String returnBitwiseAnd(String s1, String s2)
    {
        String ip = s1;
        String ip2 = s2;
        System.out.println("IN BITWISE AND : ip=" + ip +" AND ip2=" + ip2);
        String and_ip = "";
        for(int i=0;i<ip.length();i++)
        {
            char ch1 = ip.charAt(i);
            char ch2 = ip2.charAt(i);
            int val1 = Character.getNumericValue(ch1);
            int val2 = Character.getNumericValue(ch2);
            and_ip = and_ip + Integer.toString(val1 & val2);    //BITWISE AND                        
        }
        return and_ip;
    }
    
    String get_subnet_mask(String ip) //Returns subnet mask for ip address as per classful addressing scheme.
    {
        String[] sarr;
        sarr = ip.split("\\.",-2);
        if(Integer.parseInt(sarr[0])>=0 && Integer.parseInt(sarr[0])<=127) //CLASS-A
        {
            return "255.0.0.0";
        }
        else if(Integer.parseInt(sarr[0])>127 && Integer.parseInt(sarr[0])<=191) //CLASS-B
        {
            return "255.255.0.0";
        }
        else if(Integer.parseInt(sarr[0])>191 && Integer.parseInt(sarr[0])<=223) //CLASS-C
        {
            return "255.255.255.0";
        }
        else
        {
            return "INVALID ADDRESS";
        }
    }
    
    String binaryToDottedIP(String bin)
    {
        String ip = "";
        for(int i=0;i<32;i=i+8)
        {
            int val;            
            val = Integer.parseInt(bin.substring(i,i+8), 2);                           
            if(i!=24)
            {                                
                ip = ip + Integer.toString(val) + ".";
            }
            else
            {                                
                ip = ip + Integer.toString(val);
            }
        }        
        return ip;
    }
    
    int transfer_data(end_device sender, end_device receiver, String packet, end_device[] earr, Router[] r, Switch[] s)
    {
        System.out.println("ROUTER : " + this.router_id + " RECEIVED THE PACKET : " + packet);
        System.out.println("ROUTER : " + this.router_id + " REPORT : " + "LENGTH OF THE PACKET IS : " + packet.length());
        String packet_dest_nid =  returnBitwiseAnd(packet.substring(217,249), returnBinaryIP(get_subnet_mask(binaryToDottedIP(packet.substring(217,249)))));
        System.out.println("ROUTER : " + this.router_id + " REPORT : " + "ARRIVED PACKET'S DESTINATION N.I.D. IS : " + packet_dest_nid);
        
        int forwardingInterface = -1;
        int matchedEntryIndex = -1;
        int isRouterNext = 0;
        int ackToReturn = -5; 
        
        //FINDING FOR A MATCH IN THE ROUTING TABLE.
        for(int i=0;i<num_entries;i++)
        {
            //System.out.println("i = " + i + " AND ROUTING TABLE'S NID AT THIS INDEX IS: " + returnBinaryIP(routing_table[i][1]));
            if(packet_dest_nid.equals(returnBinaryIP(routing_table[i][1])))
            {
                matchedEntryIndex = i;
                System.out.println("ROUTER : " + this.router_id + " REPORT : " + "MATCHED ENTRY INDEX = " + matchedEntryIndex);
                forwardingInterface = Integer.parseInt(routing_table[i][3]);
                System.out.println("ROUTER : " + this.router_id + " REPORT : " + "PACKET DESTINATION FOUND : FORWARDING PACKET TO INTERFACE : " + forwardingInterface);
                break;
            }            
        }
        
        for(int k=0;k<r.length;k++)
        {            
            if(!r[k].equals(this))
            {
                for(int j=0;j<r[k].interfaces.length;j++)
                {                                                            
                    String s1 = routing_table[matchedEntryIndex][2];
                    String s2 = r[k].interfaces[j];                    
                    if(routing_table[matchedEntryIndex][2].equals(r[k].interfaces[j]))
                    {
                        isRouterNext = 1;
                        System.out.println("ROUTER : " + this.router_id + " REPORT : " + "FORWARDING PACKET TO ROUTER : " + r[k].router_id);
                        ackToReturn = r[k].transfer_data(sender, receiver, packet, earr, r,s);
                        System.out.println("ROUTER : " + this.router_id + " REPORT : " + " ACK : " + ackToReturn + " RECEIVED VIA ROUTER - " + r[k].router_id);
                        break;
                    }
                }
                
            }
        }
        
        
        System.out.println("ROUTER : " + this.router_id + " REPORT : " + "1 : isRouterNext = " + isRouterNext);
        if(isRouterNext!=1)
        {
            System.out.println("ROUTER : " + this.router_id + " REPORT : " + "2 : isRouterNext = " + isRouterNext);
            for(int g=0 ; g<s.length ; g++)
            {
                if(s[g].connectedRouterID == this.router_id && s[g].connectedRouterInterface == forwardingInterface)
                {
                    System.out.println("ROUTER : " + this.router_id + " REPORT : " + "FORWARDING PACKET TO SWITCH : " + s[g].switch_id);
                    ackToReturn = s[g].transfer_data(sender, receiver, packet, earr, r, s); //COMPLETE THIS
                    System.out.println("ROUTER : " + this.router_id + " REPORT : " + " ACK : " + ackToReturn + " RECEIVED VIA SWITCH.");
                }
            }
        }
        return ackToReturn;
    }
}

class end_device extends Device implements KeyListener
{
    int connectedSwitchID; //ID of the switch to which this device is connected.
    String[] frame;
    String domain_name; //Domain_name of this device in case it is a HTTP server.
    boolean isServer; // If this device is a server.
    int id;//stores the identifier of the device
    String data_to_be_sent;
    String received_data;
    final int FRAMESIZE = 184;
    final int DATAWORD = 80;
    String div="100000111";  //Standard CRC-8
    int n,k;
    final int r=8;//redundant bits.
    String[] received_frame;
    int iterator=0;
    //static int R=0;
    int R=0;
    static int R_transport = 0;
    int acknowledgment;
    int[] port_numbers = new int[2]; //MAKING TWO PROGRAMS : WEB BROWSER/SERVER AND MESSENGER.              
    int hub_port;
    String translated_message = "";
    static int guiDisp = 0;
    String url_input;
    String request_by_http_client; 
    String message_by_messenger;
    String mail;
    SMTPServer ss;
    static String sender_email_id;
    static String receiver_email_id;
    
    private static BufferedImage backgroundImage1, outputImage;
    static JTextField jtf1 = new JTextField(); // Messenger's Message input from user
    static JTextField jtf2 = new JTextField(); //Web Browser's URL input from user
    static JTextField jtf3 = new JTextField(); //Web Browser's request input from user
    static JTextField jtf4 = new JTextField(); //SMTP SENDER'S EMAIL ID I/P FROM USER
    static JTextField jtf5 = new JTextField(); //SMTP RECEIVER'S EMAIL ID I/P FROM USER
    static JTextArea jta = new JTextArea(); //SMTP CLIENT'S MAIL I/P FROM USER
    static JButton jb = new JButton("Submit");
    static JButton jb2 = new JButton("Submit");
    static JButton jb3 = new JButton("Send");
    static JEditorPane editorPane;
    
    void getSMTPServer(SMTPServer ss) //RECEIVER DEVICE GETS ITS SMTP SERVER REFERENCE
    {
        this.ss = ss;
        
    }
    
    public end_device(String s1,int s2)
    {
        mac_address=s1;
        id=s2;
    }
    
    public end_device(String s1,int s2, String ip, boolean isServer)
    {
        mac_address=s1;
        id=s2;
        ip_address = ip;
        this.isServer = isServer;
        
        //ASSIGNING PORT NUMBERS TO PROCESSES
        port_numbers[0] = 80; // HTTP CLIENT/SERVER
        port_numbers[1] = 1025; // MESSENGER PROCESS
        jtf1.addKeyListener(this);
        jtf2.addKeyListener(this);
        jb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                message_by_messenger = jtf1.getText();
                System.out.println(message_by_messenger);
            }
        });
        jb2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                url_input = jtf2.getText();
                request_by_http_client = jtf3.getText();
                System.out.println(url_input);
                System.out.println(request_by_http_client);
            }
        });
        jb3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                sender_email_id = jtf4.getText();
                receiver_email_id = jtf5.getText();
                mail = jta.getText();
                System.out.println(sender_email_id);
                System.out.println(mail);
            }
        });
    }
    
    class Messenger_Sender extends Applet implements ActionListener
    {
        TextField url_inp;
        
        public void init()
        {
            Label url = new Label("URL : ", Label.RIGHT);
            url_inp = new TextField(50);
            add(url);
            add(url_inp);
            url_inp.addActionListener(this);
        }
        
        public void actionPerformed(ActionEvent ae)
        {
            repaint();
        }
        
        public void paint(Graphics g)
        {
            g.drawString(url_inp.getText(), 6, 60);
            
        }
    }        
    public void keyReleased(KeyEvent e) 
    {  
        //String text=jtf1.getText();  
        //message_by_messenger = text;
        //System.out.println("\n\n\n\nENTERED MESSAGE : " + message_by_messenger+"\n\n\n\n");
    } 
    public void keyPressed(KeyEvent e)
    {
        
    }
    public void keyTyped(KeyEvent e)
    {
        
    }
    static class DrawPanel extends JPanel
    {   
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dim = tk.getScreenSize();

        void drawMyGUI() 
        {               
            JFrame frame = new JFrame();
            frame.setTitle("NETWORK SIMULATOR");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            DrawPanel draw = new DrawPanel();   
            frame.getContentPane().add(draw);
            frame.setSize(dim.width, dim.height);
            
            //frame.setContentPane(new JLabel(new ImageIcon("C:\\Users\\DELL\\Documents\\NetBeansProjects\\Swing_Practice\\src\\swing_practice\\bg.jpg")));

            try
            {
                backgroundImage1 = ImageIO.read(new File(".\\src\\network_simulator\\gr3.jpg"));
            }
            catch(IOException e)
            {
                System.out.println("FILE NOT FOUND.");
            }
            
            //outputImage = new BufferedImage(dim.width, dim.height, backgroundImage1.getType());
            //outputImage = Scalr.resize(backgroundImage1, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, dWidth, dHeight, bufferedImageOpArray);
                        
            this.setLayout(null);
            JLabel jl = new JLabel("NetworkSimulator");
            jl.setFont(new Font("Times New Roman", Font.BOLD, 50));
            jl.setForeground(Color.darkGray);            
            jl.setBounds(10, 600, 700, 100);
            
            JLabel jl2 = new JLabel("Messenger");
            jl2.setFont(new Font("Monotype Corsiva", Font.BOLD, 50));
            jl2.setForeground(Color.BLUE);
            jl2.setBounds(550, 20, 250, 60);
            
            JLabel jl3 = new JLabel("Message:");
            jl3.setFont(new Font("Roboto", Font.PLAIN, 14));
            jl3.setForeground(Color.BLACK);
            jl3.setBounds(10, 200, 200, 25);
            
            jtf1 = new JTextField();
            jtf1.setColumns(5);
            jtf1.setToolTipText("Enter the message , here");
            jtf1.setBounds(150, 200, 200, 20);
            
            jb.setBounds(400, 200, 100, 20);
            
            //num_routers = Integer.parseInt(jtf1.getText());
            //System.out.println(num_routers);
            
            this.add(jl);
            this.add(jl2);
            this.add(jl3);
            this.add(jb);
            this.add(jtf1);
            frame.add(this);        
            frame.setVisible(true);
            System.out.println("\n\n\n\nLEFT DRAWMYGUI\n\n\n");
        }

        public void paintComponent(Graphics g)
        {
            //g.drawString(iterator, WIDTH, WIDTH);
            g.drawImage(backgroundImage1, 0, 0, this);                                  
            g.drawLine(0, 670, dim.width, 670);
            g.drawLine(10, 100, 1330, 100);
            //g.setColor(Color.red);
            //g.fillOval(100, 50, 20,20);
        }
               
    }
    
    static class DrawPanel2 extends JPanel
    {   
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dim = tk.getScreenSize();
        
        void drawMyGUI(end_device receiver, String s) 
        {   
            Scanner ui = new Scanner(System.in);
            JFrame frame = new JFrame();
            frame.setTitle("NETWORK SIMULATOR");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            DrawPanel2 draw = new DrawPanel2();   
            frame.getContentPane().add(draw);
            frame.setSize(dim.width, dim.height);
            
            //frame.setContentPane(new JLabel(new ImageIcon("C:\\Users\\DELL\\Documents\\NetBeansProjects\\Swing_Practice\\src\\swing_practice\\bg.jpg")));

            try
            {
                backgroundImage1 = ImageIO.read(new File(".\\src\\network_simulator\\gr3.jpg"));
            }
            catch(IOException e)
            {
                System.out.println("FILE NOT FOUND.");
            }
            
            //outputImage = new BufferedImage(dim.width, dim.height, backgroundImage1.getType());
            //outputImage = Scalr.resize(backgroundImage1, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, dWidth, dHeight, bufferedImageOpArray);
                        
            this.setLayout(null);
            JLabel jl = new JLabel("NetworkSimulator");
            jl.setFont(new Font("Times New Roman", Font.BOLD, 50));
            jl.setForeground(Color.darkGray);            
            jl.setBounds(10, 600, 700, 100);
            
            JLabel jl2 = new JLabel("Messenger");
            jl2.setFont(new Font("Monotype Corsiva", Font.BOLD, 50));
            jl2.setForeground(Color.BLUE);
            jl2.setBounds(550, 20, 250, 60);
            
            JLabel jl3 = new JLabel("Received Message:");
            jl3.setFont(new Font("Roboto", Font.PLAIN, 14));
            jl3.setForeground(Color.BLACK);
            jl3.setBounds(10, 200, 200, 25);                                  
            
            System.out.println("\n\n\n\n1\n\n\n\n");
            //int num = ui.nextInt();
            System.out.println("IN GUI - NOW PRINTING : " + s);
            JLabel jl4 = new JLabel(s);
            System.out.println("\n\n\n\n1\n\n\n\n");
            //repaint();
            jl4.setFont(new Font("Roboto", Font.PLAIN, 14));
            jl4.setForeground(Color.BLACK);
            jl4.setBounds(210, 200, 900, 25);                        
            
            this.add(jl);
            this.add(jl2);
            this.add(jl3); 
            this.add(jl4);
            frame.add(this);        
            frame.setVisible(true);
            System.out.println("\n\n\n\nLEFT DRAWMYGUI\n\n\n");
        }

        public void paintComponent(Graphics g)
        {
            //g.drawString(iterator, WIDTH, WIDTH);
            g.drawImage(backgroundImage1, 0, 0, this);                                  
            g.drawLine(0, 670, dim.width, 670);
            g.drawLine(10, 100, 1330, 100);
            //g.setColor(Color.red);
            //g.fillOval(100, 50, 20,20);
        }
               
    }
    
    static class DrawPanel3 extends JPanel // FOR HTTP CLIENT : WEB BROWSER
    {   
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dim = tk.getScreenSize();

        void drawMyGUI() 
        {               
            JFrame frame = new JFrame();
            frame.setTitle("NETWORK SIMULATOR - WEB BROWSER");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            DrawPanel3 draw = new DrawPanel3();   
            frame.getContentPane().add(draw);
            frame.setSize(dim.width, dim.height);
            
            //frame.setContentPane(new JLabel(new ImageIcon("C:\\Users\\DELL\\Documents\\NetBeansProjects\\Swing_Practice\\src\\swing_practice\\bg.jpg")));

            /*try
            {
                backgroundImage1 = ImageIO.read(new File(".\\src\\network_simulator\\gr3.jpg"));
            }
            catch(IOException e)
            {
                System.out.println("FILE NOT FOUND.");
            }*/
            
            //outputImage = new BufferedImage(dim.width, dim.height, backgroundImage1.getType());
            //outputImage = Scalr.resize(backgroundImage1, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, dWidth, dHeight, bufferedImageOpArray);
                        
            this.setLayout(null);
            JLabel jl = new JLabel("NetworkSimulator");
            jl.setFont(new Font("Times New Roman", Font.BOLD, 50));
            jl.setForeground(Color.darkGray);            
            jl.setBounds(10, 600, 700, 100);
            
            JLabel jl2 = new JLabel("Web Browser");
            jl2.setFont(new Font("Cambria", Font.BOLD, 50));
            jl2.setForeground(Color.BLACK);
            jl2.setBounds(550, 20, 500, 60);
            
            JLabel jl3 = new JLabel("URL :");
            jl3.setFont(new Font("Roboto", Font.PLAIN, 14));
            jl3.setForeground(Color.BLACK);
            jl3.setBounds(10, 120, 100, 25);
            
            jtf2 = new JTextField();
            jtf2.setColumns(5);
            jtf2.setToolTipText("Enter the URL , here");
            //jtf2.setBounds(150, 200, 200, 20);
            
            jtf3 = new JTextField();
            jtf3.setColumns(5);
            jtf3.setToolTipText("Enter the request text here : \"INITIATE HTTP\"");
            //jtf3.setBounds(150, 240, 200, 20);
            
            //jb2.setBounds(400, 200, 100, 20);
            jtf2.setBounds(150, 120, 200, 25);
            jtf3.setBounds(400, 120, 200, 25);
            jb2.setBounds(650, 120, 100, 25);                       
            //num_routers = Integer.parseInt(jtf1.getText());
            //System.out.println(num_routers);
            
            this.add(jl);
            this.add(jl2);
            this.add(jl3);
            this.add(jb2);
            this.add(jtf2);
            this.add(jtf3);
            frame.add(this);        
            frame.setVisible(true);
            System.out.println("\n\n\n\nLEFT DRAWMYGUI\n\n\n");
        }

        public void paintComponent(Graphics g)
        {
            //g.drawString(iterator, WIDTH, WIDTH);
            //g.drawImage(backgroundImage1, 0, 0, this);                                  
            g.drawLine(0, 670, dim.width, 670);
            g.drawLine(10, 100, 1330, 100);
            //g.setColor(Color.red);
            //g.fillOval(100, 50, 20,20);
        }
               
    }
    
    static class DrawPanel4 extends JPanel // WEB SERVER'S INTERFACE
    {   
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dim = tk.getScreenSize();
        
        void drawMyGUI(end_device receiver, String s) 
        {   
            Scanner ui = new Scanner(System.in);
            JFrame frame = new JFrame();
            frame.setTitle("NETWORK SIMULATOR : WEB SERVER");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            DrawPanel4 draw = new DrawPanel4();   
            frame.getContentPane().add(draw);
            frame.setSize(dim.width, dim.height);
            
            //frame.setContentPane(new JLabel(new ImageIcon("C:\\Users\\DELL\\Documents\\NetBeansProjects\\Swing_Practice\\src\\swing_practice\\bg.jpg")));

            /*try
            {
                backgroundImage1 = ImageIO.read(new File(".\\src\\network_simulator\\gr3.jpg"));
            }
            catch(IOException e)
            {
                System.out.println("FILE NOT FOUND.");
            }*/
            
            frame.setBackground(Color.yellow);
            
            //outputImage = new BufferedImage(dim.width, dim.height, backgroundImage1.getType());
            //outputImage = Scalr.resize(backgroundImage1, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, dWidth, dHeight, bufferedImageOpArray);
                        
            this.setLayout(null);
            JLabel jl = new JLabel("NetworkSimulator");
            jl.setFont(new Font("Times New Roman", Font.BOLD, 50));
            jl.setForeground(Color.darkGray);            
            jl.setBounds(10, 600, 700, 100);
            
            JLabel jl2 = new JLabel("WEB SERVER");
            jl2.setFont(new Font("Cambria", Font.BOLD, 50));
            jl2.setForeground(Color.BLUE);
            jl2.setBounds(550, 20, 500, 60);
            
            JLabel jl3 = new JLabel("Received Request:");
            jl3.setFont(new Font("Roboto", Font.PLAIN, 14));
            jl3.setForeground(Color.BLACK);
            jl3.setBounds(10, 200, 200, 25);                                  
            
            System.out.println("\n\n\n\n1\n\n\n\n");
            //int num = ui.nextInt();
            System.out.println("IN GUI - NOW PRINTING : " + s);
            JLabel jl4 = new JLabel(s);
            System.out.println("\n\n\n\n1\n\n\n\n");
            //repaint();
            jl4.setFont(new Font("Roboto", Font.PLAIN, 14));
            jl4.setForeground(Color.BLACK);
            jl4.setBounds(210, 200, 900, 25);                        
            
            this.add(jl);
            this.add(jl2);
            this.add(jl3); 
            this.add(jl4);
            frame.add(this);        
            frame.setVisible(true);
            System.out.println("\n\n\n\nLEFT DRAWMYGUI\n\n\n");
        }

        public void paintComponent(Graphics g)
        {
            //g.drawString(iterator, WIDTH, WIDTH);
            g.drawImage(backgroundImage1, 0, 0, this);                                  
            g.drawLine(0, 670, dim.width, 670);
            g.drawLine(10, 100, 1330, 100);
            //g.setColor(Color.red);
            //g.fillOval(100, 50, 20,20);
        }
               
    }
        
    static class DrawPanel5 extends JPanel // FOR HTTP CLIENT : WEB BROWSER
    {   
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dim = tk.getScreenSize();

        void drawMyGUI() 
        {               
            JFrame frame = new JFrame();
            frame.setTitle("NETWORK SIMULATOR - WEB BROWSER");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            DrawPanel5 draw = new DrawPanel5();   
            frame.getContentPane().add(draw);
            frame.setSize(dim.width, dim.height);                                                
                        
            this.setLayout(null);
            JLabel jl = new JLabel("NetworkSimulator");
            jl.setFont(new Font("Times New Roman", Font.BOLD, 50));
            jl.setForeground(Color.darkGray);            
            jl.setBounds(10, 600, 700, 100);
            
            JLabel jl2 = new JLabel("Web Browser");
            jl2.setFont(new Font("Cambria", Font.BOLD, 50));
            jl2.setForeground(Color.BLACK);
            jl2.setBounds(550, 20, 500, 60);
            
            JLabel jl3 = new JLabel("URL :");            
            jl3.setFont(new Font("Roboto", Font.PLAIN, 14));
            jl3.setForeground(Color.BLACK);
            jl3.setBounds(10, 120, 100, 25);
            
            jtf2 = new JTextField();
            jtf2.setColumns(5);
            jtf2.setText("www.server.com");
            jtf2.setToolTipText("Enter the URL , here");
            //jtf2.setBounds(150, 200, 200, 20);
            
            jtf3 = new JTextField();
            jtf3.setColumns(5);
            jtf3.setText("INITIATE HTTP");
            jtf3.setToolTipText("Enter the request text here : \"INITIATE HTTP\"");
            //jtf3.setBounds(150, 240, 200, 20);
            
            //jb2.setBounds(400, 200, 100, 20);
            
            jtf2.setBounds(150, 120, 200, 25);
            jtf3.setBounds(400, 120, 200, 25);
            jb2.setBounds(650, 120, 100, 25);
            
            String url = "https://www.google.com";
            try
            {
                editorPane = new JEditorPane(url);
            }
            catch(IOException e)
            {
                System.out.println("\n\n\nNOT REACHABLE\n\n\n");
            }
            editorPane.setEditable(false);
            editorPane.setBounds(0, 170, dim.width-20, 450);
            
            //num_routers = Integer.parseInt(jtf1.getText());
            //System.out.println(num_routers);
            
            this.add(jl);
            this.add(jl2);
            this.add(jl3);
            this.add(jb2);
            this.add(jtf2);
            this.add(jtf3);
            this.add(editorPane);
            frame.add(this);        
            frame.setVisible(true);
            System.out.println("\n\n\n\nLEFT DRAWMYGUI\n\n\n");
        }

        public void paintComponent(Graphics g)
        {
            //g.drawString(iterator, WIDTH, WIDTH);
            //g.drawImage(backgroundImage1, 0, 0, this);                                  
            g.drawLine(0, 670, dim.width, 670);
            g.drawLine(10, 100, 1330, 100);
            //g.setColor(Color.red);
            //g.fillOval(100, 50, 20,20);
        }
               
    }
     
    
    static class DrawPanel6 extends JPanel // FOR SMTP CLIENT
    {   
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dim = tk.getScreenSize();

        void drawMyGUI() 
        {               
            JFrame frame = new JFrame();
            frame.setTitle("NETWORK SIMULATOR - SMTP CLIENT");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            DrawPanel6 draw = new DrawPanel6();   
            frame.getContentPane().add(draw);
            frame.setSize(dim.width, dim.height);  
            frame.setBackground(Color.magenta);
            this.setLayout(null);
            JLabel jl = new JLabel("NetworkSimulator");
            jl.setFont(new Font("Times New Roman", Font.BOLD, 50));
            jl.setForeground(Color.darkGray);            
            jl.setBounds(10, 600, 700, 100);
            
            JLabel jl2 = new JLabel("Email Service");
            jl2.setFont(new Font("Cambria", Font.BOLD, 50));
            jl2.setForeground(Color.WHITE);
            jl2.setBounds(550, 20, 500, 60);
            
            JLabel jl3 = new JLabel("Email ID :");
            jl3.setFont(new Font("Roboto", Font.PLAIN, 14));
            jl3.setForeground(Color.BLACK);
            jl3.setBounds(10, 120, 100, 25);
            
            jtf4 = new JTextField("Sender's Email here");
            jtf4.setColumns(5);
            jtf4.setToolTipText("Enter your Email ID , here");
            //jtf2.setBounds(150, 200, 200, 20);
            
            jtf5 = new JTextField("Receiver's Email here");
            jtf5.setColumns(5);
            jtf5.setToolTipText("Enter the receiver's Email ID , here");
            
            jta = new JTextArea();
            jta.setColumns(5);
            jta.setToolTipText("Enter the Mail here : ");
            //jtf3.setBounds(150, 240, 200, 20);
            
            //jb2.setBounds(400, 200, 100, 20);
            jtf4.setBounds(150, 120, 200, 25);
            jtf5.setBounds(400, 120, 200, 25);
            jta.setBounds(10, 170, dim.width - 20, 200);
            jb3.setBounds(dim.width/2, 400, 100, 25);                       
            //num_routers = Integer.parseInt(jtf1.getText());
            //System.out.println(num_routers);
            
            this.add(jl);
            this.add(jl2);
            this.add(jl3);
            this.add(jb3);
            this.add(jtf4);
            this.add(jtf5);
            this.add(jta);
            frame.add(this);        
            frame.setVisible(true);
            System.out.println("\n\n\n\nLEFT DRAWMYGUI\n\n\n");
        }

        public void paintComponent(Graphics g)
        {
            //g.drawString(iterator, WIDTH, WIDTH);
            //g.drawImage(backgroundImage1, 0, 0, this);                                  
            g.drawLine(0, 670, dim.width, 670);
            g.drawLine(10, 100, 1330, 100);
            //g.setColor(Color.red);
            //g.fillOval(100, 50, 20,20);
        }
               
    }
    
    
    static class DrawPanel7 extends JPanel // FOR SMTP SERVER
    {   
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dim = tk.getScreenSize();

        void drawMyGUI(String s) 
        {               
            JFrame frame = new JFrame();
            frame.setTitle("NETWORK SIMULATOR - SMTP SERVER");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JOptionPane.showMessageDialog(frame, "You have received an Email from : " + sender_email_id);
            DrawPanel7 draw = new DrawPanel7();   
            frame.getContentPane().add(draw);
            frame.setSize(dim.width, dim.height);  
            frame.setBackground(Color.magenta);
            this.setLayout(null);
            JLabel jl = new JLabel("NetworkSimulator");
            jl.setFont(new Font("Times New Roman", Font.BOLD, 50));
            jl.setForeground(Color.darkGray);            
            jl.setBounds(10, 600, 700, 100);
            
            JLabel jl2 = new JLabel("Email Service");
            jl2.setFont(new Font("Cambria", Font.BOLD, 50));
            jl2.setForeground(Color.WHITE);
            jl2.setBounds(550, 20, 500, 60);
            
            JLabel jl3 = new JLabel("Email :");
            jl3.setFont(new Font("Roboto", Font.PLAIN, 14));
            jl3.setForeground(Color.BLACK);
            jl3.setBounds(10, 120, 100, 25);
            
            JLabel jl4 = new JLabel(s);
            jl4.setFont(new Font("Roboto", Font.PLAIN, 14));
            jl4.setForeground(Color.BLACK);
            jl4.setBounds(10, 150, 1000, 25);
                       
            
            //jb2.setBounds(400, 200, 100, 20);
            jtf4.setBounds(150, 120, 200, 25);
            jta.setBounds(10, 170, dim.width - 20, 200);
            jb3.setBounds(dim.width/2, 400, 100, 25);                       
            //num_routers = Integer.parseInt(jtf1.getText());
            //System.out.println(num_routers);
            
            this.add(jl);
            this.add(jl2);
            this.add(jl3);
            this.add(jl4);            
            frame.add(this);        
            frame.setVisible(true);
            System.out.println("\n\n\n\nLEFT DRAWMYGUI\n\n\n");
        }

        public void paintComponent(Graphics g)
        {
            //g.drawString(iterator, WIDTH, WIDTH);
            //g.drawImage(backgroundImage1, 0, 0, this);                                  
            g.drawLine(0, 670, dim.width, 670);
            g.drawLine(10, 100, 1330, 100);
            //g.setColor(Color.red);
            //g.fillOval(100, 50, 20,20);
        }
               
    }
    
    void display_status()//to display the status of the endstation
    {
        System.out.println("The MAC Address is:-"+mac_address);
        System.out.println("The Identifier is:-"+id);
        System.out.println("The IP Address is:-" + ip_address);        
    }
    void binarize(String ch)//converts the datastream into binary form
    {
        int len=ch.length();
        String datastring="";//The string that contains the input message converted to binary form
        for(int i=0;i<len;i++)
        {
            int n=(int)ch.charAt(i);//Integer variable that holds the ascii value of the character of the string
            String s=Integer.toString(n,2);//The string that contains the binary representation of the each character without zeroes being appended
            int numzeroes=8-s.length();//contains the no.of zeroes to be appended
            String s1="";//string of length 8 with x=zeroes appended
            for(int a=0; a<numzeroes; a++)
            {
                s1 = s1+ "0";
            }
            for(int a=0;a<s.length();a++)
            {
                s1=s1+s.charAt(a);
            }
            //System.out.println(s1);
            datastring=datastring+s1;
        }
        data_to_be_sent=datastring;//stores the final data in binary form to be sent
        //System.out.println("The Datastream for the message is:-"+datastring);
        System.out.println("\n\n\nBINARIZE REPORT : DATA TO BE SENT IS : "+ data_to_be_sent + "\n\n\n");
    }
    
     String binarizePortNum(int ch)//converts the port number into binary form
    {
        String dd = Integer.toString(ch,2);
        String new_dd = "";
        if(dd.length()==16)
        {
            System.out.println("PORT NUMBER = " + dd);
            return dd;
        }
        else
        {
            int numzeros = 16 - dd.length();
            for(int i=0; i<numzeros; i++)
            {
                new_dd = new_dd + "0";
            }
            new_dd = new_dd + dd;
            System.out.println("PORT NUMBER = " + dd);
            return new_dd;
        }
    }
    
    String binarizeARP(String ch)//converts the datastream into binary form
    {
        int len=ch.length();
        String datastring="";//The string that contains the input message converted to binary form
        for(int i=0;i<len;i++)
        {
            int n=(int)ch.charAt(i);//Integer variable that holds the ascii value of the character of the string
            String s=Integer.toString(n,2);//The string that contains the binary representation of the each character without zeroes being appended
            int numzeroes=8-s.length();//contains the no.of zeroes to be appended
            String s1="";//string of length 8 with x=zeroes appended
            for(int a=0; a<numzeroes; a++)
            {
                s1 = s1+ "0";
            }
            for(int a=0;a<s.length();a++)
            {
                s1=s1+s.charAt(a);
            }
            //System.out.println(s1);
            datastring=datastring+s1;
        }
        System.out.println("The arpQuery in binary is : " + datastring);
        return datastring;        
    }
           
    String sendARPpacket(String sender_ip, String dest_ip, String SMAC, end_device[] earr)
    {
        String arpQuery = this.binarizeARP("Who is ") + dest_ip + this.binarizeARP(" ? Tell ") + sender_ip;        
        System.out.println("\n"+"-------------------------------------------------------------------------------ARP-QUERY-----------------------------------------------------------------------");
        System.out.println("END-STATION : " + this.id + " REPORT : BROADCASTING ARP QUERY IN THE NETWORK.");
        for(int i=0; i<earr.length; i++)
        {
            String reply = earr[i].receiveARPpacket(arpQuery);
            if(reply.length()==48)
            {
                System.out.println("\nEND-STATION : " + this.id + " REPORT : MAC ADDRESS " + reply + " FOUND FOR SOUGHT IP ADDRESS : " + dest_ip);
                return reply;
            }
        }
        return "MAC COULD NOT BE FOUND FOR INTENDED IP ADDRESS.";
    }
    
    String binaryToDottedIP(String bin)
    {
        String ip = "";
        for(int i=0;i<32;i=i+8)
        {
            int val;            
            val = Integer.parseInt(bin.substring(i,i+8), 2);                           
            if(i!=24)
            {                                
                ip = ip + Integer.toString(val) + ".";
            }
            else
            {                                
                ip = ip + Integer.toString(val);
            }
        }        
        return ip;
    }
    
    String returnBinaryIP(String ip) //returns binary of given IP address as A.B.C.D
    {
        String[] ip_part = ip.split("\\.", -2);
        String binary_ip = "";
        for(int i=0;i<4;i++)
        {
            //System.out.println("i=" + i);
            String str = Integer.toString(Integer.parseInt(ip_part[i]),2);
            int strlen = str.length();
            String fr = "";
            if(strlen!=8)
            {
                int numzeros = 8 - strlen;
                int j=0;
                while(j!=numzeros)
                {
                    fr=fr+"0";
                    j++;
                }
                fr=fr+str;                
                binary_ip = binary_ip + fr;
            }
            else
            {
                binary_ip = binary_ip + str;
            }
        }
        return binary_ip;
    }
    
    String receiveARPpacket(String arpQuery)
    {
        System.out.println("END-STATION : " + this.id + " RECEIVED ARP-QUERY FROM " + binaryToDottedIP(arpQuery.substring(152)));        
        if(arpQuery.substring(0,56).equals("01010111011010000110111100100000011010010111001100100000") && arpQuery.substring(56, 88).equals(this.returnBinaryIP(this.ip_address)))
        {
            System.out.println("END-STATION : " + this.id + " SENDING ITS MAC ADDRESS TO " + binaryToDottedIP(arpQuery.substring(152)) + " AS THIS END-STATION IS THE INTENDED RECEIVER OF ARP QUERY.");
            return this.mac_address;
        }
        else
        {
            System.out.println("INTENDED ARP QUERY IS NOT INTENDED FOR DEVICE : " + this.id);
            return "";
        }
    }
    void send_data(String A,String B,hub h, end_device[] e) //For sending data to the hub, as in star topology.
    {
       String final_datastring = B;
       final_datastring = final_datastring + data_to_be_sent;
       h.transfer_data(final_datastring, e);       
   }
   
    void send_data(String A, String B, end_device h, end_device[] e) //For sending data to other node, as in mesh topology.
    {
       String final_datastring = B;
       final_datastring = final_datastring + data_to_be_sent;
       h.receive_data(final_datastring);
   }
    boolean receive_data(String rdata)
    {
       
        if(rdata.substring(0,48).equals(mac_address))
        {
            received_data = rdata.substring(48, rdata.length());
            translate();
            System.out.println("DATA RECEIVED BY STATION :" + id + " AND DATA IS :" + received_data);
            return true;
        }
        else
        {
            return false;
        }
   }
    String translate()
    {       
       String ts = "";
       for(int i=0;i<received_data.length();i=i+8)
       {
           String s = received_data.substring(i,i+8);
           int num = Integer.parseInt(s,2);
           char ch = (char)num;
           String sm = Character.toString(ch);
           ts = ts + sm;
           /*if(i==0)
               translated_message = sm;
           else
               translated_message += sm;*/
       }
       //translated_message += ts;
       System.out.println("THE DATA RECEIVED AT : " + id + " AND DATA RECEIVED IS : " + ts);
       return ts;
   }
                
    /*------------------------------------------------------------ APPLICATION LAYER ---------------------------------------------------------------------*/
    void applicationLayer()
    {
        
    }      
   /*-------------------------------------------------------------DATA LINK LAYER ------------------------------------------------------------------------*/
    String CRC_imp(String dw)
    {
       
       k=dw.length();
       n=k+r;
       String dividend=dw; String div_part="";
       for(int i=1;i<=r;i++)
       {
           dividend=dividend+"0";
       }
       //System.out.println("The dividend at the sender's side is: " + dividend);
       for(int i=0;i<k;i++)
       {
          
           if(i==0)
           {
               div_part=dividend.substring(0,9);  //divisor length
               if(div_part.charAt(0)=='1')
               div_part=XOR(div_part,div);
           else
               div_part=XOR(div_part,"000000000");  
           }
           else 
           {
              div_part=div_part+dividend.charAt(i+8);
              if(div_part.charAt(0)=='1')
                  div_part=XOR(div_part,div);
              else
                  div_part=XOR(div_part,"000000000");  
           }
           
       }
      dw=dw+div_part;
      return div_part;
    }
    String XOR(String A,String B)
    {
        String result="";
        for(int i=0;i<A.length();i++)
        {
            if((A.charAt(i)=='0'&&B.charAt(i)=='1')||(A.charAt(i)=='1'&&B.charAt(i)=='0'))
            {
                result=result+ "1";
            }
            else
                result=result+ "0";
        }
        return result.substring(1);
    }
    
   void frameDLL(String SMAC,String DMAC, end_device receiver)
   {
       int fullframes=data_to_be_sent.length()/DATAWORD;
       int rem=data_to_be_sent.length()%DATAWORD;
       int numzeroes=DATAWORD-rem;
       if (rem!=0)
        frame=new String[fullframes+1];
       else
           frame=new String[fullframes];
       int k=0;
       int i=0;
       for(i=0;i<fullframes;i++)
       {
           if (i%2==0)
               frame[i]="0"+SMAC+DMAC+data_to_be_sent.substring(k,k+80)+CRC_imp(data_to_be_sent.substring(k,k+80));
           else
               frame[i]="1"+SMAC+DMAC+data_to_be_sent.substring(k,k+80)+CRC_imp(data_to_be_sent.substring(k,k+80));
           k=k+80;
       }
       String lastframe;
       if(i%2==0)
            lastframe="0"+SMAC+DMAC;
       else
           lastframe="1"+SMAC+DMAC;
       for(int j=0;j<numzeroes;j++)
       {
           lastframe+="0";
       }
       if(rem!=0)
            frame[i]=lastframe+data_to_be_sent.substring(k)+CRC_imp(data_to_be_sent.substring(k));
       //MIGHT CAUSE ERROR
       receiver.iterator = 0;
       receiver.R = 0;
       ///////////////////
   }
   
   void frameDLL(String SMAC,String DMAC, end_device receiver, String sender_ip, String receiver_ip, end_device[] earr, String portnum)
   {
       int fullframes=data_to_be_sent.length()/DATAWORD;
       int rem=data_to_be_sent.length()%DATAWORD;
       int numzeroes=DATAWORD-rem;
       if (rem!=0)
           frame=new String[fullframes+1];
       else
           frame=new String[fullframes];
       int k=0;
       int i=0;
       
       //CALLING ARP METHOD
       String dmac = this.sendARPpacket(sender_ip, receiver_ip, SMAC, earr); //OBTAINING DESTIONATION MAC ADDRESS THROUGH ARP QUERY.
       
       for(i=0;i<fullframes;i++)
       {
           System.out.println("i=" + i);
           if (i%2==0){
               System.out.println("IN IF");
               frame[i]="0"+SMAC+DMAC+data_to_be_sent.substring(k,k+80)+CRC_imp(data_to_be_sent.substring(k,k+80))+sender_ip+receiver_ip+portnum+portnum+"0";
               System.out.println("\n\n 1 : FRAME " + i + " IS : " + frame[i]);
           }
           else{
               System.out.println("IN ELSE");
               frame[i]="1"+SMAC+DMAC+data_to_be_sent.substring(k,k+80)+CRC_imp(data_to_be_sent.substring(k,k+80))+sender_ip+receiver_ip+portnum+portnum+"1";
               System.out.println("\n\n 2 : FRAME " + i + " IS : " + frame[i]);
           }
           k=k+80;
       }
       String lastframe;
       String tl = "";
       if(i%2==0)
       {
            lastframe="0"+SMAC+DMAC;
            tl = "0";
       }
       else
       {
           lastframe="1"+SMAC+DMAC;
           tl = "1";
       }
       for(int j=0;j<numzeroes;j++)
       {
           lastframe+="0";
       }
       if(rem!=0)
       {
            frame[i]=lastframe+data_to_be_sent.substring(k)+CRC_imp(data_to_be_sent.substring(k))+sender_ip+receiver_ip+portnum+portnum+tl;
            System.out.println("\n\n 3 : FRAME " + i + " IS : " + frame[i]);
       }
       //MIGHT CAUSE ERROR
       receiver.iterator = 0;
       receiver.R = 0;
       ///////////////////
       System.out.println("\n\n\nMADE FRAMES, "+ frame.length+" IN ALL.\n\n\n");       
       /*for(int kk = 0; kk<frame.length ;k++)
       {         
           System.out.println(frame[kk]);
       }*/
       System.out.println("\n\n\n\n\n\n");
   }
        
   int transport_flow_control_feedback(String frame_recv) // RECEIVER'S TRANSPORT LAYER FLOW CONTROL FUNCTIONALITY.
   {
       System.out.println("SEGMENT ARRIVED AT END-STATION" + this.id + " - TRANSPORT LAYER");
        char ch = frame_recv.charAt(281);
        int chnum = Character.getNumericValue(ch); // SEQUENCE NUMBER (TRANSPORT LAYER) OF THE RECEIVED SEGMENT.
        int c=0;    
        System.out.println("R_transport = " + R_transport);
        System.out.println("chnum = " + chnum);
        if(R_transport==chnum)
        {    
            boolean a=Error_check(frame_recv.substring(97, 185));            
            if(a==true)
            {                
                R_transport=(R_transport+1)%2;
                String rec_data = frame_recv.substring(97,177);                                                                                
                System.out.println("RECEIVER'S TRANSPORT LAYER REPORT: NO ERROR IN RECEIVED SEGMENT");                                                                              
                return R_transport;
            }
            else
            {
                System.out.println("RECEIVER'S TRANSPORT LAYER REPORT: ERROR IN RECEIVED SEGMENT");
                c=-1;
            }
        }
        else         
            c=-1;        
        if(c==-1)
        {
            try
            {
                System.out.println("SLEEPING FOR 1 SEC NOW : TRANSPORT LAYER");
                Thread.sleep(1000);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
        }        
        return c;        
   }
   
   void transport_flow_control(end_device receiver, String segment) // SENDER'S TRANSPORT LAYER FLOW CONTROL FUNCTIONALITY.
   {       
        int ackTransport;       
        int S_transport;
        char ch=segment.charAt(281); 
        S_transport = Character.getNumericValue(ch);
        System.out.println("--------------------------------------------------------------------------------------------------------------");
        System.out.println("SENDER'S TRANSPORT LAYER REPORT : SEGMENT " + S_transport + " SENT");
        double it=System.nanoTime();          
        ackTransport = receiver.transport_flow_control_feedback(segment);
        System.out.println("SENDER'S TRANSPORT LAYER REPORT : THE TRANSPORT-LAYER ACK RECEIVED IS : " + ackTransport);          
        double ft=System.nanoTime();
        double time=(double)(ft-it)/1000000;
        System.out.println("SENDER'S TRANSPORT LAYER REPORT : THE TIME ELAPSED IN RECEIVING THE TRANSPORT-LAYER ACK IS: "+time);
        if(time>1000.0)
        {
            System.out.println("SENDER'S TRANSPORT LAYER REPORT : THE SEGMENT IS BEING RESENT TO THE RECEIVER.");                          
        }
        if(ackTransport==(S_transport+1)%2)
        {
           S_transport = ackTransport;            
           System.out.println("SENDER'S TRANSPORT LAYER REPORT : ACK " + ackTransport + " RECEIVED");
        }                 
   }
    void Sender_DLL(end_device e)
    {
        int S,ACK;
        double timePassed;
        e.received_frame = new String[frame.length];
        String[] frame_copy= new String[frame.length];
        for(int i=0;i<frame.length;i++)
            frame_copy[i] = frame[i];
        frame_copy=produce_Random_Error(frame_copy);
        int i = 0;
        do 
        {
          char ch=frame_copy[i].charAt(0); 
          S = Character.getNumericValue(ch);
          System.out.println("--------------------------------------------------------------------------------------------------------------");
          System.out.println("FRAME " + S + " SENT");
          double it=System.nanoTime();
          ACK=e.Receiver_DLL(frame_copy[i]);
          //System.out.println("SENT FRAME'S DATA: " + frame_copy[i].substring(97) + " OF LENGTH: " + frame_copy[i].substring(97).length());
          double ft=System.nanoTime();
          double time=(double)(ft-it)/1000000;
          System.out.println("The time elapsed is "+time);
          if(time>15.0)
          {
              System.out.println("The frame "+i+" is being resent.");
              frame_copy=frame; 
              continue;
          }
          if(ACK==(S+1)%2)
          {
            S=ACK;
            i=i+1;
            System.out.println("ACK " + ACK + " RECEIVED");
          }
          else
          {
              frame_copy=frame;
              continue;
          }
        }while(i<frame_copy.length);
        
        
    }
            
    void Sender_DLL(Switch[] sw, end_device receiver, end_device[] earr, Router[] r,  String macSender, String macReceiver, String IPSender, String IPReceiver, int portNumChoice, DomainNameServer dns) //OVERLOADED : FOR SENDING DATA TO END_DEVICE THROUGH SWITCH
    {
        Scanner ui = new Scanner(System.in);
        int S,ACK;
        double timePassed;    
                                
        //System.out.println("\n\nLENGTH OF RECEIVED_FRAME IS: " + receiver.received_frame.length+"\n\n");
        String server_ip;
        if(portNumChoice == 1025) //MESSENGER
        {
            DrawPanel dr = new DrawPanel();
            dr.drawMyGUI();  
            System.out.println("ENTER ANY INTEGER TO CONTINUE:");
            int mk1 = ui.nextInt(); //TO WAIT FOR USER'S INPUT : IF WE DIDN'T TAKE THIS CODE WOULD HAVE CONTINUED WITHOUT TAKING USER INPUT FROM GUI.
            System.out.println(message_by_messenger);
            this.binarize(message_by_messenger);
            String portnum = binarizePortNum(portNumChoice);
            this.frameDLL(macSender, macReceiver, receiver, IPSender, IPReceiver, earr, portnum);
        }
        
        else if(portNumChoice == 80) // HTTP
        {
            //String domain_name_entered_by_user : TO BE INPUT FROM THE GUI OF WEB BROWSER
            
            DrawPanel3 dr = new DrawPanel3(); // WEB BROWSER, i.e. HTTP CLIENT.
            dr.drawMyGUI();  
            System.out.println("PRESS ANY KEY TO CONTINUE:");
            int mk2 = ui.nextInt(); //TO WAIT FOR USER'S INPUT : IF WE DIDN'T TAKE THIS CODE WOULD HAVE CONTINUED WITHOUT TAKING USER INPUT FROM GUI.
            
            this.binarize(request_by_http_client);
            System.out.println("SENDER DLL REPORT : INITIATED REQUEST : " + request_by_http_client);
            String domain_name_entered_by_user = url_input; //ENTERED BY USER IN WEB BROWSER
            System.out.println("\n\nTHE URL INPUT BY USER IS : " + domain_name_entered_by_user);
            String DNSresponse = dns.DNSquery(domain_name_entered_by_user);
            if(DNSresponse.equals("ERROR 404"))
            {
                System.out.println("\n\nNETWORK-SIMULATOR WEB BROWSER ERROR : NOTHING'S HERE : UNKNOWN DOMAIN NAME : ERROR 404\n\n");
                System.exit(1);
            }
            else
            {
                server_ip = DNSresponse;
                System.out.println("\n\nTHE IP ADDRESS FOUND FOR THE URL INPUT BY USER IS : " + server_ip);
            }
            
            String portnum = binarizePortNum(portNumChoice);
            this.frameDLL(macSender, macReceiver, receiver, IPSender, IPReceiver, earr, portnum);
        }
        
        else if(portNumChoice == 25) // SMTP
        {                        
            DrawPanel6 dr3 = new DrawPanel6(); // SMTP CLIENT.
            dr3.drawMyGUI();  
            System.out.println("PRESS ANY KEY TO CONTINUE:");
            int mk2 = ui.nextInt(); //TO WAIT FOR USER'S INPUT : IF WE DIDN'T TAKE THIS CODE WOULD HAVE CONTINUED WITHOUT TAKING USER INPUT FROM GUI.            
            this.binarize(mail);
            System.out.println("\n\n\nMAIL TO BE SENT : " + mail +"\n\n\n");
            String portnum = binarizePortNum(portNumChoice);
            this.frameDLL(macSender, macReceiver, receiver, IPSender, IPReceiver, earr, portnum);
            //SMTPClient sc = new SMTPClient(this);
            //SMTPServer ss = new SMTPServer();
            //sc.sendMail(ss, receiver);
        }
        receiver.received_frame = new String[frame.length];        
                
        String[] frame_copy = new String[frame.length];
        for(int i=0;i<frame.length;i++)
            frame_copy[i] = frame[i];
        frame_copy = produce_Random_Error(frame_copy);
        int i = 0;
        int mySwitch=0;
        for(int m=0;m<sw.length;m++)
        {
            if(sw[m].switch_id == this.connectedSwitchID)
            {
                System.out.println("END STATION : " + id + " REPORT : " + " MATCH FOUND : THIS END-STATION IS CONNECTED TO SWITCH : " + m);
                mySwitch = m;
            }
        }
        do 
        {
          char ch=frame_copy[i].charAt(0); 
          S = Character.getNumericValue(ch);
          System.out.println("--------------------------------------------------------------------------------------------------------------");
          System.out.println("SENDER REPORT : FRAME " + S + " SENT");
          double it=System.nanoTime();
          //ACK=e.Receiver_DLL(frame_copy[i]);
          ACK = sw[mySwitch].transfer_data(this, receiver, frame_copy[i], earr, r, sw);
          System.out.println("SENDER REPORT : THE ACK RECEIVED IS : " + ACK);
          //System.out.println("SENT FRAME'S DATA: " + frame_copy[i].substring(97) + " OF LENGTH: " + frame_copy[i].substring(97).length());
          double ft=System.nanoTime();
          double time=(double)(ft-it)/1000000;
          System.out.println("SENDER REPORT : The time elapsed is "+time);
          if(time>8000.0)
          {
              System.out.println("SENDER REPORT : The frame "+i+" is being resent.");
              frame_copy=frame; 
              
              //ADD ON
              if(i!=frame_copy.length)
                this.transport_flow_control(receiver, frame_copy[i]);
              continue;
          }
          if(ACK==(S+1)%2)
          {
            S=ACK;
            i=i+1;
            System.out.println("SENDER REPORT : ACK " + ACK + " RECEIVED");
            
            //ADD ON
            if(i!=frame_copy.length)
                this.transport_flow_control(receiver, frame_copy[i]);
          }
          else
          {
              frame_copy=frame;
              continue;
          }
        }while(i<frame_copy.length);               
    }
            
    void Sender_DLL(hub thisdevicehub, end_device sender, end_device receiver, hub[] h, end_device[] earr, Switch sw) //OVERLOADED : FOR SENDING DATA TO END_DEVICE THROUGH SECOND TOPLOGY.
    {
        int S,ACK;
        double timePassed;
        receiver.received_frame = new String[frame.length];
        //System.out.println("\n\nLENGTH OF RECEIVED_FRAME IS: " + receiver.received_frame.length+"\n\n");
        String[] frame_copy = new String[frame.length];
        for(int i=0;i<frame.length;i++)
            frame_copy[i] = frame[i];
        frame_copy = produce_Random_Error(frame_copy);
        int i = 0;
        do 
        {
          char ch=frame_copy[i].charAt(0); 
          S = Character.getNumericValue(ch);
          System.out.println("--------------------------------------------------------------------------------------------------------------");
          System.out.println("FRAME " + S + " SENT");
          double it=System.nanoTime();
          //ACK=e.Receiver_DLL(frame_copy[i]);
          
          // TO BE CORRECTED YET.
          //ACK = thisdevicehub.transfer_data(this, receiver, frame_copy[i], earr, h);
          thisdevicehub.caller = 0;
          ACK = thisdevicehub.transfer_data(earr, sender, receiver, sw, frame_copy[i], h);
          //////
          
          System.out.println("THE ACK RECEIVED IS : " + ACK);
          //System.out.println("SENT FRAME'S DATA: " + frame_copy[i].substring(97) + " OF LENGTH: " + frame_copy[i].substring(97).length());
          double ft=System.nanoTime();
          double time=(double)(ft-it)/1000000;
          System.out.println("The time elapsed is "+time);
          if(time>70.0)
          {
              System.out.println("The frame "+i+" is being resent.");
              frame_copy=frame; 
              continue;
          }
          if(ACK==(S+1)%2)
          {
            S=ACK;
            i=i+1;
            System.out.println("ACK " + ACK + " RECEIVED");
          }
          else
          {
              frame_copy=frame;
              continue;
          }
        }while(i<frame_copy.length);
        
        
    }
                
    String[] produce_Random_Error(String[] arr)
    {
        Random rand=new Random();
        int r=rand.nextInt(arr.length);
        //int p=rand.nextInt(arr[r].length());
        int p = rand.nextInt(79);
        p = p + 97;
        if(arr[r].charAt(p)=='1')
            arr[r]=arr[r].substring(0,p)+'0'+arr[r].substring(p+1);
        else
            arr[r]=arr[r].substring(0,p)+'1'+arr[r].substring(p+1);
        
        System.out.println("THE ERROR PRODUCED IS IN FRAME "+r);
       
        return arr;
    }
    int Receiver_DLL(String frame_recv)
    {        
        System.out.println("PACKET ARRIVED AT END-STATION : " + this.id);
        char ch = frame_recv.charAt(0);
        int chnum = Character.getNumericValue(ch);
        int c=0;
        //System.out.println("DEST MAC: " + frame_recv.substring(49, 97));
        //System.out.println("THIS DEVICE MAC: " + this.mac_address);
        if(!frame_recv.substring(49, 97).equals(this.mac_address)) //IF DESTINATION ADDRESS IN FRAME DOESN'T MATCH WITH RECEIVER'S MAC, IT WILL REJECT THE FRAME.
        {
            System.out.println("PACKET NOT MEANT FOR THIS END STATION :" + this.id);
            return -2;
        } // RECEIVER TELLS THAT I AM NOT THE INTENDED RECEIVER FOR THIS FRAME.
        if(R==chnum)    
        {    
            boolean a=Error_check(frame_recv.substring(97, 185));            
            if(a==true)
            {
                guiDisp++;
                R=(R+1)%2;
                received_data = frame_recv.substring(97,177);
                String tt = translate();
                System.out.println("\n\n\n\n\n\n tt = "+ tt +"\n\n\n\n\n");
                translated_message += tt;
                System.out.println("\n\n\n\n\n\n translated_message = "+ translated_message +"\n\n\n\n\n");
                received_frame[iterator++]=frame_recv.substring(97,185);
                System.out.println("RECEIVER'S REPORT: NO ERROR IN RECEIVED FRAME");
                
                System.out.println("\n\n\n1:received_frame.length = " + received_frame.length+"\n\n\n");
                System.out.println("\n\n\n1:guiDisp = " + guiDisp+"\n\n\n");
                if(guiDisp == received_frame.length)
                {
                    //System.out.println("\n\n\n2:received_frame.length = " + received_frame.length);
                    //DrawPanel2 dr = new DrawPanel2();    
                    //System.out.println("\n\n\n\n\n PASSING "+ this.translated_message +" TO GUI.\n\n\n\n");
                    //dr.drawMyGUI(this, this.translated_message);
                    
                    System.out.println("\n\n\n RECEIVED FRAME DEST. PORT NO. IS : "+Integer.parseInt(frame_recv.substring(265, 281), 2)+"\n\n\n");
                    System.out.println(frame_recv.substring(265, 281));
                    if(Integer.parseInt(frame_recv.substring(265, 281), 2) == 1025)
                    {
                        System.out.println("\n\n\nPORT NO. 1025 REQUESTED");
                        System.out.println("\n\n\n2:received_frame.length = " + received_frame.length);
                        DrawPanel2 dr = new DrawPanel2();    
                        System.out.println("\n\n\n\n\n PASSING "+ this.translated_message +" TO GUI.\n\n\n\n");
                        dr.drawMyGUI(this, this.translated_message);
                    }

                    if(Integer.parseInt(frame_recv.substring(265, 281), 2) == 80) //FOR HTTP SERVER
                    {
                        System.out.println("\n\n\nPORT NO. 80 REQUESTED");
                        System.out.println("\n\n\n2:received_frame.length = " + received_frame.length);
                        DrawPanel4 dr2 = new DrawPanel4();    
                        System.out.println("\n\n\n\n\n PASSING "+ this.translated_message +" TO GUI.\n\n\n\n");
                        dr2.drawMyGUI(this, this.translated_message);
                        
                        DrawPanel5 dr3 = new DrawPanel5();
                        dr3.drawMyGUI();
                    }      
                    
                    if(Integer.parseInt(frame_recv.substring(265, 281), 2) == 25) //FOR SMTP SERVER
                    {
                        System.out.println("\n\n\nPORT NO. 25 REQUESTED");
                        System.out.println("\n\n\n2:received_frame.length = " + received_frame.length);
                        DrawPanel7 dr2 = new DrawPanel7();    
                        System.out.println("\n\n\n\n\n PASSING "+ this.translated_message +" TO GUI.\n\n\n\n");
                        dr2.drawMyGUI(this.translated_message);                                                
                    }
                }                                
                return R;
            }
            else
            {
                System.out.println("RECEIVER'S REPORT: ERROR IN RECEIVED FRAME");
                c=-1;
            }
        }
        else 
        {
            c=-1;
        }
        if(c==-1)
        {
            try
            {
                Thread.sleep(10000);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("\n\n\n1:received_frame.length = " + received_frame.length+"\n\n\n");
        System.out.println("\n\n\n1:guiDisp = " + guiDisp+"\n\n\n");
        if(guiDisp == received_frame.length)
        {
            if(Integer.parseInt(frame_recv.substring(265, 281), 2) == 1025)
            {
                System.out.println("\n\n\nPORT NO. 1025 REQUESTED");
                System.out.println("\n\n\n2:received_frame.length = " + received_frame.length);
                DrawPanel2 dr = new DrawPanel2();    
                System.out.println("\n\n\n\n\n PASSING "+ this.translated_message +" TO GUI.\n\n\n\n");
                dr.drawMyGUI(this, this.translated_message);
            }
            
            if(Integer.parseInt(frame_recv.substring(265, 281), 2) == 80) //FOR HTTP SERVER
            {
                System.out.println("\n\n\nPORT NO. 80 REQUESTED");
                System.out.println("\n\n\n2:received_frame.length = " + received_frame.length);
                DrawPanel4 dr2 = new DrawPanel4();    
                System.out.println("\n\n\n\n\n PASSING "+ this.translated_message +" TO GUI.\n\n\n\n");
                dr2.drawMyGUI(this, this.translated_message);
            }
        }
        return c;
        
    }
   boolean Error_check(String codeword)
   {
        int len_codeword=codeword.length();
        String div_part="";
       for(int i=0;i<len_codeword-8;i++)
       {   
           if(i==0)
           {
               div_part=codeword.substring(0,9); 
               if(div_part.charAt(0)=='1')
                   div_part=XOR(div_part,div);
               else
                   div_part=XOR(div_part,"000000000");  
           }
           else 
           {
              div_part=div_part+codeword.charAt(i+8);
              if(div_part.charAt(0)=='1')
                  div_part=XOR(div_part,div);
              else
                  div_part=XOR(div_part,"000000000");  
           }
           
       }
        //System.out.println("ERROR CHECK REPORT: THE REMAINDER IS:" +div_part);
        if(div_part.equals("00000000"))
        { 
            return true;
        }
        else 
        {
            return false;
        }
   }
   /*-----------------------------------------------------------------------------------------------------------------------------------------------------*/
}

/* ------------------------------------------------------------------HUB------------------------------------------------------------------------------- */
class hub extends Device
{
    int numports;
    char[] port = new char[numports];
    int start; //STARTING INDEX : INDEX OF FIRST END DEVICE CONNECTED TO THIS HUB IN THE ARRAY OF END-DEVICE OBJECTS.
    int end; //ENDING INDEX : INDEX OF LAST END DEVICE CONNECTED TO THIS HUB IN THE ARRAY OF END-DEVICE OBJECTS.
    int caller;
    hub(String mac, int no)
    {
        mac_address = mac;
        numports = no;
    }
    hub(String mac, int no, int start, int end)
    {
        mac_address = mac;
        numports = no;
        this.start = start;
        this.end = end;
    }
    
    void transfer_data(String data, end_device[] e) // HUB
    {
        for(int i = 0;  i < e.length; i++)
        {
            System.out.println("Sending data to device: " + i);
            boolean b = e[i].receive_data(data);
            if(b == true)
            {
                System.out.println("Data received by station :" + i);
            }
            else
            {
                System.out.println("Data rejected by station :" +i);
            }
            
        }
        
    }
    
    int transfer_data(end_device[] earr, end_device sender, end_device receiver, Switch sw, String data, hub[] h)
    {
        int ACK = -3;
        System.out.println("INSIDE HUB.");
        for(int i=start;i<=end;i++)
        {
            if(i==sender.id)
                continue;
            System.out.println("HUB REPORT : Sending data to device: " + i);
            ACK = earr[i].Receiver_DLL(data);
            if(ACK == 0 || ACK == 1 || ACK == -1)
            {
                System.out.println("HUB REPORT : Data accepted by device - " + i);
                return ACK;
            }
            else
            {
                System.out.println("HUB REPORT : Data rejected by device - " + i);
            }
        }
        if(caller!=2)
        {
            ACK = sw.transfer_data(earr, h, sender, receiver, data, this);
            if(ACK == 0 || ACK == 1 || ACK == -1)
            {
                System.out.println("HUB REPORT : Data accepted by some other hub connected to the switch. ");
                return ACK;
            }
            else
            {
                return ACK;
            }
        }
        else
            return ACK;
    }
}
/*-----------------------------------------------------------------------------HUB ENDS----------------------------------------------------------------- */

/*---------------------------------------------------------------------------SWITCH----------------------------------------------------------------------*/
class Switch extends Device
{
    int switch_id;
    int connectedRouterID;
    int connectedRouterInterface;
    int numports;
    String received_frame;
    int startfrom; //STARTING INDEX FOR THIS  END-DEVICE ARRAY.
    HashMap<String,Integer> mac_table=new HashMap<>();
    static int sk = 0;
    static Scanner uit = new Scanner(System.in);
    Switch(int devicenum,String mac)
    {
        mac_address=mac;
        numports=devicenum;
        switch_id = sk++;
        System.out.println("ENTER ROUTER ID OF ROUTER CONNECTED TO THIS SWITCH: ");
        connectedRouterID = uit.nextInt();        
        uit.nextLine();
        System.out.println("ENTER , TO WHICH INTERFACE OF SAID ROUTER IS THIS SWITCH CONNECTED ? : ");
        connectedRouterInterface = uit.nextInt();
        uit.nextLine();
        
    }
    Switch(int devicenum,String mac, int sf, ArrayList<String> input)
    {
        startfrom = sf;    
        mac_address=mac;
        numports=devicenum;
        switch_id = sk++;        
        //System.out.println("ENTER ROUTER ID OF ROUTER CONNECTED TO THIS SWITCH: ");
        //connectedRouterID = uit.nextInt();
        
        connectedRouterID = Integer.parseInt(input.get(NetworkSimulator.inpin++));
        System.out.println("CONNECTED ROUTER ID : " + connectedRouterID);
        //uit.nextLine();
        //System.out.println("ENTER , TO WHICH INTERFACE OF SAID ROUTER IS THIS SWITCH CONNECTED ? : ");
        //connectedRouterInterface = uit.nextInt();
        connectedRouterInterface = Integer.parseInt(input.get(NetworkSimulator.inpin++));
        System.out.println("CONNECTED ROUTER INTERFACE : " + connectedRouterInterface);
        //uit.nextLine();
    }
    /* void assignport(end_device[] obj) // KINDLY OVERLOAD THIS TO INCLUDE HUBS AS WELL.
    {
        for(int i=0;i<obj.length;i++)
        {
            obj[i].switch_port = i;
        }
    } */ 
    
    void assignport(end_device[] obj) // THIRD SUBMISSION.
    {
        for(int i=startfrom;i<startfrom+numports;i++)
        {
            obj[i].switch_port = i;
            obj[i].connectedSwitchID = this.switch_id;
        }
    }
    
    void assignport(hub[] h) // 
    {
        for(int i=0;i<h.length;i++)
            h[i].switch_port = i;
    }
    int transfer_data(end_device sender, end_device receiver, String frame, end_device[] earr) //Returns ACK to sender, forwards data to receiver.
    {
        String sender_mac = frame.substring(1,49);
        String dest_mac = frame.substring(49,97);
        int ACK = -3;
        //received_frame = frame;
        if(!mac_table.containsKey(sender_mac))
        {
            System.out.println(" SWITCH REPORT : MAKING NEW ENTRY IN MAC ADDRESS TABLE : " + sender_mac + " AT PORT NO. : " + sender.switch_port);
            mac_table.put(sender_mac,sender.switch_port);
        }
        else
        {
            System.out.println("MAC ADDRESS OF SENDER FOUND IN MAC TABLE CORRESPONDING TO PORT NO. " + sender.switch_port);
        }
        if(!mac_table.containsKey(dest_mac))
        {
            for(int i=0 ; i<earr.length; i++)
            {
                if(i==sender.switch_port)
                    continue;
                //receiver.R=0;
                ACK = earr[i].Receiver_DLL(frame);
                if(ACK==-1 || ACK==1 || ACK==0)
                {
                    System.out.println("DATA ACCEPTED BY DEVICE - " + i);
                    System.out.println(" SWITCH REPORT : MAKING NEW ENTRY IN MAC ADDRESS TABLE : " + dest_mac + " AT PORT NO. : " + earr[i].switch_port);
                    mac_table.put(dest_mac, earr[i].switch_port);
                    System.out.println(mac_table);
                    return ACK;
                }
                else
                {
                    System.out.println("DATA REJECTED BY DEVICE - " + i);
                }
            }
        }
        else
        {
            System.out.println("MAC ADDRESS OF DESTINATION FOUND IN MAC TABLE CORRESPONDING TO PORT NO. " + receiver.switch_port);
            System.out.println("DATA ACCEPTED BY DEVICE -  "  + receiver.switch_port);
            //receiver.R=0;
            ACK = receiver.Receiver_DLL(frame);
        }
        return ACK;
    }
    int transfer_data(end_device[] earr, hub[] h, end_device sender, end_device receiver, String frame, hub senderhub)
    {
        System.out.println("INSIDE SWITCH.");
        String sender_mac=frame.substring(1,49);
        String dest_mac=frame.substring(49,97);
        int ACK = -3; //DEFAULT VALUE.
        senderhub.caller = 2;
        if(!mac_table.containsKey(sender_mac))
        {
            System.out.println(" SWITCH REPORT : MAKING NEW ENTRY IN MAC ADDRESS TABLE : " + sender_mac + " AT PORT NO. : " + senderhub.switch_port);
            mac_table.put(sender_mac , senderhub.switch_port);
        }
        else
        {
            System.out.println("SWITCH REPORT : MAC ADDRESS OF SENDER FOUND IN MAC TABLE CORRESPONDING TO PORT NO. " + senderhub.switch_port);
        }
        
        if(!mac_table.containsKey(dest_mac))
        {
            for(int i=0; i<h.length; i++)
            {
                ACK = h[i].transfer_data(earr, sender, receiver, this, frame, h);
                System.out.println("HUB RETURNED ACK : " + ACK);
                if(ACK==-1 || ACK==1 || ACK==0)
                {
                    //System.out.println("DATA ACCEPTED BY A DEVICE ON HUB - " + i);
                    System.out.println(" SWITCH REPORT : MAKING NEW ENTRY IN MAC ADDRESS TABLE : " + dest_mac + " AT PORT NO. : " + h[i].switch_port);
                    mac_table.put(dest_mac, h[i].switch_port);
                    System.out.println(mac_table);
                    return ACK;
                }
                else
                {
                    System.out.println("SWITCH REPORT : DATA REJECTED BY DEVICES OF THE HUB - " + i);
                }
            }
        }
        else
        {
            System.out.println("SWITCH REPORT : MAC ADDRESS OF DESTINATION FOUND IN MAC TABLE CORRESPONDING TO PORT NO.: " + mac_table.get(receiver.mac_address));
            System.out.println("SWITCH REPORT : DATA ACCEPTED BY DEVICE -  "  + receiver.id);
            //receiver.R=0;
            ACK = senderhub.transfer_data(earr, sender, receiver, this, frame, h);
        }
        return ACK;
        
    }
                  
    //SUBMISSION-THREE OVERLOADED
    int transfer_data(end_device sender, end_device receiver, String frame, end_device[] earr, Router[] r, Switch[] sw) //Returns ACK to sender, forwards data to receiver.
    {
        String sender_mac = frame.substring(1,49);
        String dest_mac = frame.substring(49,97);
        int ACK = -3;
        //received_frame = frame;
        if(!mac_table.containsKey(sender_mac))
        {
            System.out.println(" SWITCH REPORT : MAKING NEW ENTRY IN MAC ADDRESS TABLE : " + sender_mac + " AT PORT NO. : " + sender.switch_port);
            mac_table.put(sender_mac,sender.switch_port);
        }
        else
        {
            System.out.println("SWITCH REPORT : MAC ADDRESS OF SENDER FOUND IN MAC TABLE CORRESPONDING TO PORT NO. " + sender.switch_port);
        }
        if(!mac_table.containsKey(dest_mac))
        {
            for(int i=startfrom ; i<startfrom+(earr.length/2); i++)
            {
                if(i==sender.switch_port)
                    continue; //DON'T SEND BACK THE FRAME TO SENDER.
                //receiver.R=0;
                ACK = earr[i].Receiver_DLL(frame);
                if(ACK==-1 || ACK==1 || ACK==0)
                {
                    System.out.println("SWITCH REPORT : DATA ACCEPTED BY DEVICE - " + i);
                    System.out.println(" SWITCH REPORT : MAKING NEW ENTRY IN MAC ADDRESS TABLE : " + dest_mac + " AT PORT NO. : " + earr[i].switch_port);
                    mac_table.put(dest_mac, earr[i].switch_port);
                    System.out.println(mac_table);
                    return ACK;
                }
                else
                {
                    System.out.println("SWITCH REPORT : DATA REJECTED BY DEVICE - " + i);
                }
            }
            
        }
        else
        {
            System.out.println("SWITCH REPORT : MAC ADDRESS OF DESTINATION FOUND IN MAC TABLE CORRESPONDING TO PORT NO. " + receiver.switch_port);
            System.out.println("SWITCH REPORT : DATA ACCEPTED BY DEVICE -  "  + receiver.switch_port);
            //receiver.R=0;
            ACK = receiver.Receiver_DLL(frame);
            if(ACK==-1 || ACK==0 || ACK==1)
                return ACK;            
        }
        
        for(int i=0;i<r.length;i++)
        {
            if(r[i].router_id == this.connectedRouterID)
            {
                System.out.println("SWITCH REPORT : PACKET IS BEING FORWARDED NOW TO THE ROUTER : " + r[i].router_id);
                int ackFromRouter = r[i].transfer_data(sender, receiver, frame, earr, r, sw);
                System.out.println("SWITCH REPORT : ACK : " + ackFromRouter + " VIA ROUTER : " + r[i].router_id + " RECEIVED.");
                return ackFromRouter;
            }
        }
        
        return ACK;
    }
    
}



/*-------------------------------------------------------------------------------------------------------------------------------------------------------*/




public class NetworkSimulator
{
    String message;
    int numStn;
    int topology;
    int rxid;
    static int jk = 0;
    public static Scanner userInput=new Scanner(System.in);
    static int inpin = 0;
    String generate_random_mac()
    {
        Random rr = new Random();
        String mac = "";
        for(int i=0;i<48;i++)
        {
            int m = rr.nextInt(2);
            String ss = Integer.toString(m);
            mac = mac + ss;
        }
        
        String hex_mac = "";
        String[] namepiks2 = mac.split("(?<=\\G.{4})");
        for(int i=0;i<namepiks2.length;i++)
        {
            switch(namepiks2[i])
            {
                case "0000":
                    hex_mac = hex_mac + "0";
                    break;
                case "0001":
                    hex_mac = hex_mac + "1";
                    break;
                case "0010":
                    hex_mac = hex_mac + "2";
                    break;
                case "0011":
                    hex_mac = hex_mac + "3";
                    break;
                case "0100":
                    hex_mac = hex_mac + "4";
                    break;
                case "0101":
                    hex_mac = hex_mac + "5";
                    break;
                case "0110":
                    hex_mac = hex_mac + "6";
                    break;  
                case "0111":
                    hex_mac = hex_mac + "7";
                    break;    
                case "1000":
                    hex_mac = hex_mac + "8";
                    break;
                case "1001":
                    hex_mac = hex_mac + "9";
                    break;    
                case "1010":
                    hex_mac = hex_mac + "A";
                    break;
                case "1011":
                    hex_mac = hex_mac + "B";
                    break;
                case "1100":
                    hex_mac = hex_mac + "C";
                    break;
                case "1101":
                    hex_mac = hex_mac + "D";
                    break;
                case "1110":
                    hex_mac = hex_mac + "E";
                    break;
                case "1111":
                    hex_mac = hex_mac + "F";
                    break;
            }
        }
        String hex = "";
        String[] ssar = hex_mac.split("(?<=\\G.{2})");
        for(int i=0;i<ssar.length;i++)
        {
            if(i!=ssar.length -1)
                hex = hex + ssar[i] + ":";
            else
                hex = hex + ssar[i];
        }
                                                                  
        return mac;
    }
    
    void second_topology()
    {
        System.out.println("--------------------------------------------  SECOND TOPOLOGY   ------------------------------------------------------");
        System.out.println("PLEASE ENTER THE NUMBER OF HUBS TO BE CONNECTED DIRECTLY TO THE SWITCH");
        int hubnum = userInput.nextInt(); //No. of hubs to be connected directly to the switch.
        hub[] hub = new hub[hubnum];    //HUBs    
        NetworkSimulator ns = new NetworkSimulator(); 
        Switch switchDev = new Switch(hubnum, ns.generate_random_mac()); //SWITCH
        int[] n = new int[hubnum];
        int devicenum=0;
        for(int i=0; i<hubnum; i++)
        {
            System.out.println("PLEASE ENTER THE NUMBER OF DEVICES TO BE CONNECTED WITH HUB - " + i);
            int a = userInput.nextInt();
            n[i] = a + 1; //ONE PORT FOR THE SWITCH.
            devicenum += a;
        }
        userInput.nextLine();
        end_device[] earr = new end_device[devicenum]; //END-DEVICES
        for(int i=0;i<devicenum;i++)
        {
            earr[i] = new end_device(ns.generate_random_mac(), i);
        }
        int k=0;
        for(int i=0; i<hubnum; i++)
        {
            hub[i] = new hub(ns.generate_random_mac(), n[i], k, k+n[i]-1);
            k = k + n[i];
        }
        
        System.out.println("Enter the data: ");
        String userData = userInput.nextLine();
        
        System.out.println("Please enter sending station identifier in the range 0 to " + (devicenum-1) + " : ");
        int sender = userInput.nextInt();
        
        System.out.println("Please enter receiving station identifier in the range 0 to " + (devicenum-1) + " : ");
        int receiver = userInput.nextInt();
        
        ////////////////////////////////////////////////////////////////////
        System.out.println("Please enter HUB no.:");
        int hno = userInput.nextInt();
        
        //int receiver = userInput.nextInt();
            try
            {
                earr[sender].binarize(userData);
            }
            catch(ArrayIndexOutOfBoundsException f)
            {
                System.out.println("1 : YOU HAVE ENTERED INVALID IDENTIFIER OUTSIDE THE RANGE. RE-RUN THE PROGRAM.");
            }
            try
            {
                earr[sender].frameDLL(earr[sender].mac_address, earr[receiver].mac_address, earr[receiver]);
            }
            catch(ArrayIndexOutOfBoundsException f)
            {
                System.out.println("2 : YOU HAVE ENTERED INVALID IDENTIFIER OUTSIDE THE RANGE. RE-RUN THE PROGRAM.");
            }


            
            //earr[sender].Sender_DLL(switchDev, earr[receiver], earr);
           earr[sender].Sender_DLL(hub[hno], earr[sender], earr[receiver], hub, earr, switchDev);
           System.out.println("---------------------------------------------------------------------------------------------------------------------");
           System.out.println("THE BROADCAST DOMAIN FOR THE NETWORK IS = 1");
           System.out.println("THE COLLISION DOMAIN FOR THE NETWORK IS = " + hubnum);        
           System.out.println("DO YOU WISH TO SEND MORE DATA? 1 FOR YES, 0 FOR NO.");
            int ch = userInput.nextInt();
            if(ch==1)
                this.second_topology();
            else
                return;
        
    }
    
    String assign_apipa() // ASSIGNS STATIC LINK LOCAL ADDRESS (APIPA)
    {
        String ini = "169.254.0.";
        int num = NetworkSimulator.jk++;
        String app = Integer.toString(num);
        ini = ini + app;
        return ini;
    }
    
    String returnBinaryIP(String ip) //returns binary of given IP address as A.B.C.D
    {
        String[] ip_part = ip.split("\\.", -2);
        String binary_ip = "";
        for(int i=0;i<4;i++)
        {
            String str = Integer.toString(Integer.parseInt(ip_part[i]),2);
            int strlen = str.length();
            String fr = "";
            if(strlen!=8)
            {
                int numzeros = 8 - strlen;
                int j=0;
                while(j!=numzeros)
                {
                    fr=fr+"0";
                    j++;
                }
                fr=fr+str;                
                binary_ip = binary_ip + fr;
            }
        } 
        return binary_ip;
    }

    public static void main(String[] args) 
    {
       /* SUBMISSION ONE INPUT 
        String ch;//String that takes user input
        System.out.println("Enter the data you want to transmit");
        ch=userInput.nextLine();
        System.out.println("Enter the number of devices");
        int devicenum=userInput.nextInt();//stores the no.of devices in the network
        end_device[] node=new end_device[devicenum];//creating an array of objects of end_device class
        Network_Simulator obj=new Network_Simulator();//to generate random MAC Address for the central Hub
        String mmm=obj.generate_random_mac();//stores the random MAC Addrress of the hub
        hub central_hub = new hub(mmm);
        int ID=0;//to give a unique identifier to each device 
        for(int i=0;i<devicenum;i++)
        {            
            String mac=obj.generate_random_mac();
            node[i]=new end_device(mac,ID++);//creating the objects
            node[i].display_status();
        }
        //System.out.println("You are based at end station 0.");
        System.out.println("Enter the sending station identifier:");
        int sender_id = userInput.nextInt();
        node[sender_id].binarize(ch);
        
        //System.out.println("Please enter the type of topology for data transfer:");
        //System.out.println("Enter 1 for STAR TOPOL0GY and 2 for MESH TOPOLOGY.");
        //int choice=userInput.nextInt();//stores the type of topology
        System.out.println("Please enter the station identifier to which you wish to send data:");
        int receiving_stn = userInput.nextInt(); //stores the id of receiving station
        //System.out.println(receiving_stn);
        node[sender_id].frameDLL(node[sender_id].mac_address, node[receiving_stn].mac_address);
        
        //System.out.println("\n\nTHE FRAMES ARE:\n");
        //for(int i=0;i<node[sender_id].frame.length;i++)
        //{
            //System.out.println(node[sender_id].frame[i]);
        //}
        System.out.println("\n\n");
        
        //if (choice==1) 
            //node[sender_id].send_data(node[sender_id].mac_address, node[receiving_stn].mac_address, central_hub, node);
        //else
            //node[sender_id].send_data(node[sender_id].mac_address, node[receiving_stn].mac_address, node[receiving_stn], node);
        node[sender_id].Sender_DLL(node[receiving_stn]);


        */
        
        
        //SUBMISSION-2 (DLL) USER INPUT PART
        
    /*    System.out.println("PLEASE ENTER NO. OF END DEVICES TO BE CONNECTED TO THE SWITCH:");
        int devicenum = userInput.nextInt();
        end_device[] earr = new end_device[devicenum]; // ARRAY OF END-DEVICES.
        NetworkSimulator nobj = new NetworkSimulator(); 
        System.out.println("--------------------------------------------------------------------------------------------------------------------------");
        System.out.println("STATUS OF THE CREATED STATIONS:");
        for(int i=0;i<devicenum;i++)
        {
            earr[i] = new end_device(nobj.generate_random_mac(), i);
            earr[i].display_status();
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------------");
        Switch sw = new Switch(devicenum, nobj.generate_random_mac()); // SWITCH
        sw.assignport(earr);
        userInput.nextLine();
        System.out.println("PLEASE ENTER DATA TO SEND:");
        String inputData = userInput.nextLine();
        System.out.println("DATA TO BE SENT : " + inputData);
        System.out.println("YOU HAVE DEVICE IDENTIFIERS RANGING FROM 0 TO " + (devicenum-1) + ". KINDLY ENTER ONE OF THESE AS SENDER.");
        int sender_id = userInput.nextInt();
        try
        {
            earr[sender_id].binarize(inputData);
        }
        catch(ArrayIndexOutOfBoundsException f)
        {
            System.out.println("YOU HAVE ENTERED INVALID IDENTIFIER OUTSIDE THE RANGE. RE-RUN THE PROGRAM.");
        }
        
        System.out.println("YOU HAVE DEVICE IDENTIFIERS RANGING FROM 0 TO " + (devicenum-1) + ". KINDLY ENTER ONE OF THESE AS RECEIVER.");
        int receiver_id = userInput.nextInt();
        try
        {
            earr[sender_id].frameDLL(earr[sender_id].mac_address, earr[receiver_id].mac_address, earr[receiver_id]);
        }
        catch(ArrayIndexOutOfBoundsException f)
        {
            System.out.println("YOU HAVE ENTERED INVALID IDENTIFIER OUTSIDE THE RANGE. RE-RUN THE PROGRAM.");
        }
        
        
        try{
        earr[sender_id].Sender_DLL(sw, earr[receiver_id], earr);}
        catch(ArrayIndexOutOfBoundsException f)
        {
            System.out.println("YOU HAVE ENTERED INVALID IDENTIFIER OUTSIDE THE RANGE. RE-RUN THE PROGRAM.");
        } 
      while(true)  
      {  
        System.out.println("SEND MORE DATA? 0 FOR NO, 1 FOR YES: ");
        int Choice = userInput.nextInt();
        userInput.nextLine();
        if(Choice==0)
            System.exit(1);
        else
        {
            System.out.println("PLEASE ENTER DATA TO SEND:");
            String inputDat = userInput.nextLine();
            System.out.println("DATA TO BE SENT : " + inputDat);
            System.out.println("YOU HAVE DEVICE IDENTIFIERS RANGING FROM 0 TO " + (devicenum-1) + ". KINDLY ENTER ONE OF THESE AS SENDER.");
            int sender = userInput.nextInt();
            try
            {
                earr[sender].binarize(inputDat);
            }
            catch(ArrayIndexOutOfBoundsException f)
            {
                System.out.println("1 : YOU HAVE ENTERED INVALID IDENTIFIER OUTSIDE THE RANGE. RE-RUN THE PROGRAM.");
            }

            System.out.println("YOU HAVE DEVICE IDENTIFIERS RANGING FROM 0 TO " + (devicenum-1) + ". KINDLY ENTER ONE OF THESE AS RECEIVER.");
            int receiver = userInput.nextInt();
            try
            {
                earr[sender].frameDLL(earr[sender].mac_address, earr[receiver].mac_address, earr[receiver]);
            }
            catch(ArrayIndexOutOfBoundsException f)
            {
                System.out.println("2 : YOU HAVE ENTERED INVALID IDENTIFIER OUTSIDE THE RANGE. RE-RUN THE PROGRAM.");
            }


            
            earr[sender].Sender_DLL(sw, earr[receiver], earr);
            System.out.println("THE BROADCAST DOMAIN FOR THE NETWORK IS = 1");
            System.out.println("THE COLLISION DOMAIN FOR THE NETWORK IS = " + devicenum);
            //catch(ArrayIndexOutOfBoundsException f)
            //{
              //  System.out.println("3 : YOU HAVE ENTERED INVALID IDENTIFIER OUTSIDE THE RANGE. RE-RUN THE PROGRAM.");
            //}
        }
      }*/
      //new Network_Simulator().second_topology();
        
        
        
        
        
        
        
                
        //THIRD SUBMISSION
        
        
        
        String thisLine = null;
        Reader rr = null;
        ArrayList<String> input = new ArrayList<String>();
        try
        {
            rr = new FileReader("C:\\Users\\DELL\\Documents\\NetBeansProjects\\network_simulator\\src\\network_simulator\\sample_new.txt");           
        }
        catch(FileNotFoundException e)
        {
            System.out.println("INPUT FILE NOT FOUND");
        }
        
        try {                                 
         BufferedReader br;
         br = new BufferedReader(rr);
         
         while ((thisLine = br.readLine()) != null) 
         {
            //System.out.println(thisLine);
            input.add(thisLine);
         }       
        } 
        catch(Exception e) 
        {
            e.printStackTrace();
        }                            
        System.out.println("INPUT SIZE" + input.size());
        ////////////////////////////
        int nr;
        //int inpin = 0;
        //System.out.println("PLEASE ENTER THE NUMBER OF ROUTERS FOR THE TOPOLOGY : ");
        //nr = userInput.nextInt();
        nr = Integer.parseInt(input.get(inpin++));
        //System.out.println("PLEASE ENTER NO. OF END DEVICES TO BE CONNECTED TO THE TWO SWITCHES:");
        //int devicenum = userInput.nextInt();
        int devicenum = Integer.parseInt(input.get(inpin++));
        end_device[] earr = new end_device[devicenum]; // ARRAY OF END-DEVICES.        
        NetworkSimulator nobj = new NetworkSimulator(); 
        System.out.println("--------------------------------------------------------------------------------------------------------------------------");
        System.out.println("STATUS OF THE CREATED STATIONS:");
        for(int i=0;i<devicenum;i++)
        {
            //System.out.println("ASSIGN IP ADDRESS TO DEVICE? 1 FOR YES, 0 FOR NO. IF YOU DO NOT ENTER THE IP, DEVICE WILL ASSIGN ITSELF APIPA.");
            //int ch = userInput.nextInt();
            int ch = Integer.parseInt(input.get(inpin++));
            //userInput.nextLine();
            String ip;
            if(ch==0)
            {
                ip = nobj.assign_apipa();
            }
            else
            {
                //System.out.println("ENTER DEVICE IP AS PER CLASSFUL ADDRESSING SCHEME : ");
                //ip = userInput.nextLine();
                ip = input.get(inpin++);
            }
            if(i!=devicenum-1)
                earr[i] = new end_device(nobj.generate_random_mac(), i, ip, false);
            else
                earr[i] = new end_device(nobj.generate_random_mac(), i, ip, true);
            earr[i].display_status();
        }   
        Router[] r = new Router[nr];
        for(int i=0;i<nr;i++)
        {
            r[i] = new Router(nobj.generate_random_mac(), input);
        }
        //Router r1 = new Router(nobj.generate_random_mac());
        //Router r2 = new Router(nobj.generate_random_mac());
        System.out.println("--------------------------------------------------------------------------------------------------------------------------");
        //Switch sw1 = new Switch(devicenum/2, nobj.generate_random_mac(), 0); // SWITCH-1
        //Switch sw2 = new Switch(devicenum/2, nobj.generate_random_mac(), devicenum/2); // SWITCH-1
        Switch[] sw = new Switch[2];
        for(int i=0;i<2;i++)
        {
            sw[i] = new Switch(devicenum/2, nobj.generate_random_mac(), i*(devicenum/2), input);
            sw[i].assignport(earr);
        }        
        //userInput.nextLine();
        //System.out.println("HAD YOU OPTED FOR DYNAMIC ROUTING ? IF YES PRESS 1, ELSE PRESS 0.");
        //int dynch = userInput.nextInt();
        int dynch = Integer.parseInt(input.get(inpin++));
        //userInput.nextLine();
        if(dynch==1)
        {
            System.out.println("ENTERED 1.");
            for(int i=0;i<r.length;i++)
            {
                System.out.println("CALLING RIP METHOD.");
                r[i].rip(r);
            }
        }
        //System.out.println("PLEASE ENTER DATA TO SEND:");
        //String inputData = userInput.nextLine();
        System.out.println("INPIN = " + inpin);
        
        
        //DNS initialization
        
        DomainNameServer dns = new DomainNameServer(earr);
        
        /* 
        REMOVED THIS TO TAKE INPUT DATA FROM GUI.
        String inputData = input.get(inpin++);
        System.out.println("DATA TO BE SENT : " + inputData); 
        */
        
        
        //System.out.println("YOU HAVE DEVICE IDENTIFIERS RANGING FROM 0 TO " + (devicenum-1) + ". KINDLY ENTER ONE OF THESE AS SENDER.");
        //int sender_id = userInput.nextInt();              
        int sender_id = Integer.parseInt(input.get(inpin++));
        System.out.println("YOU HAVE PORT NUMBERS 80 (HTTP) , 25 (SMTP) AND 1025 (MESSENGER) ON SENDING DEVICE. KINDLY ENTER ONE OF THESE TO ENABLE CORRESPONDING COMMUNICATION: ");
        int portNumChoice = userInput.nextInt();        
        /* 
        REMOVED THIS TO TAKE INPUT DATA FROM GUI.
        earr[sender_id].binarize(inputData);       
        */
                
        
        //System.out.println("YOU HAVE DEVICE IDENTIFIERS RANGING FROM 0 TO " + (devicenum-1) + ". KINDLY ENTER ONE OF THESE AS RECEIVER.");
        //int receiver_id = userInput.nextInt();        
        int receiver_id = Integer.parseInt(input.get(inpin));
        String sender_ip = nobj.returnBinaryIP(earr[sender_id].ip_address);        
        String receiver_ip = nobj.returnBinaryIP(earr[receiver_id].ip_address);
        
        /*
        REMOVED THIS TO TAKE INPUT DATA FROM GUI.
        earr[sender_id].frameDLL(earr[sender_id].mac_address, earr[receiver_id].mac_address, earr[receiver_id], sender_ip, receiver_ip, earr);                                                    
        */
        
        /*if(sender_id>=0 && sender_id<devicenum/2)        
            earr[sender_id].Sender_DLL(sw, earr[receiver_id], earr, r);
        else        
            earr[sender_id].Sender_DLL(sw, earr[receiver_id], earr, r);*/
        
        
        if(sender_id>=0 && sender_id<devicenum/2)        
            earr[sender_id].Sender_DLL(sw, earr[receiver_id], earr, r, earr[sender_id].mac_address, earr[receiver_id].mac_address, sender_ip, receiver_ip, portNumChoice, dns);
        else        
            earr[sender_id].Sender_DLL(sw, earr[receiver_id], earr, r, earr[sender_id].mac_address, earr[receiver_id].mac_address, sender_ip, receiver_ip, portNumChoice, dns);
        
        
        
        //SUBMISSION THREE PART ENDS HERE.
                   
      /*while(true)  
      {  
        System.out.println("SEND MORE DATA? 0 FOR NO, 1 FOR YES: ");
        int Choice = userInput.nextInt();
        userInput.nextLine();
        if(Choice==0)
            System.exit(1);
        else
        {
            System.out.println("PLEASE ENTER DATA TO SEND:");
            String inputDat = userInput.nextLine();
            System.out.println("DATA TO BE SENT : " + inputDat);
            System.out.println("YOU HAVE DEVICE IDENTIFIERS RANGING FROM 0 TO " + (devicenum-1) + ". KINDLY ENTER ONE OF THESE AS SENDER.");
            int sender = userInput.nextInt();
            try
            {
                earr[sender].binarize(inputDat);
            }
            catch(ArrayIndexOutOfBoundsException f)
            {
                System.out.println("YOU HAVE ENTERED INVALID IDENTIFIER OUTSIDE THE RANGE. RE-RUN THE PROGRAM.");
            }

            System.out.println("YOU HAVE DEVICE IDENTIFIERS RANGING FROM 0 TO " + (devicenum-1) + ". KINDLY ENTER ONE OF THESE AS RECEIVER.");
            int receiver = userInput.nextInt();
            try
            {
                String sender_ip = nobj.returnBinaryIP(earr[sender].ip_address);        
                String receiver_ip = nobj.returnBinaryIP(earr[receiver].ip_address);
                earr[sender_id].frameDLL(earr[sender].mac_address, earr[receiver].mac_address, earr[receiver], sender_ip, receiver_ip);
            }
            catch(ArrayIndexOutOfBoundsException f)
            {
                System.out.println("YOU HAVE ENTERED INVALID IDENTIFIER OUTSIDE THE RANGE. RE-RUN THE PROGRAM.");
            }


            try
            {
                if(sender>=0 && sender<devicenum/2)        
                    earr[sender].Sender_DLL(sw1, earr[receiver], earr);
                else        
                    earr[sender].Sender_DLL(sw2, earr[receiver], earr);
            }
            catch(ArrayIndexOutOfBoundsException f)
            {
                System.out.println("YOU HAVE ENTERED INVALID IDENTIFIER OUTSIDE THE RANGE. RE-RUN THE PROGRAM.");
            }
        }//end of ELSE 
    }//end of WHILE */ 
}
}