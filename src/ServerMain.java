import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author Long Yiyi
 * @date 2022/05/12 19:21
 */

public class ServerMain extends JFrame implements ActionListener, KeyListener {

    private JTextArea jta;             //文本域
    private JScrollPane jsp;           //滚动条
    private JPanel jp;                 //面板
    private JTextField jtf;            //文本框
    private JButton jbtn;              //按钮
    private BufferedWriter bw = null;  //输出流
    private static int serverPort;     //服务端的端口号

    //使用static静态代码块读取配置文件，仅在类加载时自动执行一次
    static {
        Properties prop = new Properties();
        try {
            prop.load(new FileReader("chat.properties")); //读取配置文件
            serverPort = Integer.parseInt(prop.getProperty("serverPort")); //读取serverPort属性的值
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //构造方法
    public ServerMain() {
        //在构造方法中初始化组件:
        jta = new JTextArea(); //初始化文本框
        jta.setEditable(false);//设置文本框默认不可编辑
        jsp = new JScrollPane(jta); //初始化滚动条时，将文本域添加到滚动条中实现文本滚动效果
        jp = new JPanel(); //初始化面板
        jtf = new JTextField(10); //初始化文本框，设置文本框长度为10
        jbtn = new JButton("发送");	//初始化按钮
        //将文本框和按钮添加至面板中:
        jp.add(jtf);
        jp.add(jbtn);
        //将以上组件添加到窗体中，并设置布局:
        this.add(jsp, BorderLayout.CENTER);
        this.add(jp,BorderLayout.SOUTH);
        //设置窗体的标题、大小、位置:
        this.setTitle("lyy的聊天室 服务端");
        this.setSize(300,300); //设置大小
        this.setLocation(300,300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //设置窗体关闭时程序退出
        this.setVisible(true); //设置窗体可见


        /*** TCP服务端start ***/
        //给发送按钮绑定一个监听点击事件:
        jbtn.addActionListener(this); //让当前类去监听，当前类须实现ActionListener这个接口中的actionPerformed()方法
        //给文本框绑定一个回车键点击监听事件:
        jtf.addKeyListener(this);
        try {
            //1、创建一个服务端的套接字（socket）
            ServerSocket serverSocket = new ServerSocket(serverPort);//端口号，如：8888

            //2、等待客户端的连接
            Socket socket = serverSocket.accept();

            //3、获取Socket通道的输入流
            InputStream in = socket.getInputStream();
            //输入流实现数据的读取，如何读取：一行一行的读取。用BufferedReader类的readLine()方法可以做到
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            //4、获取socket通道的输出流
            //输出流写数据也是写一行换一行。使用BufferedWriter类的newLine()方法
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //循环，逐行读取用户的输入内容，当用户点击发送按钮时，socket通道读入数据后立刻写到文本域中
            String line = null;
            while((line = br.readLine()) != null) {
                //将读取到的该行数据拼接到文本域中显示,并换行
                jta.append(line + System.lineSeparator());
            }
            //5、关闭socket通道
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        /*** TCP服务端end ***/
    }

    //将文本框的内容发送到socket通道中
    private void sendDataToSocket() {
        //1、获取文本框中要发送的内容
        String text = jtf.getText();
        //2、拼接需要发送的数据内容并换行
        text = "server对client说：" + text;
        //3、将输入内容显示到服务器端窗体的文本域中
        jta.append(text + System.lineSeparator());
        try {
            //4、发送
            bw.write(text);
            bw.newLine();//换行
            bw.flush();//刷新
            //5、发送成功后清空文本框内容
            jtf.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //监听发送按钮点击事件
    @Override
    public void actionPerformed(ActionEvent arg0) {
//		System.out.println("发送按钮被点击了......");
        sendDataToSocket();
    }

    //监听键盘点击事件
    @Override
    public void keyPressed(KeyEvent e) {
        //判断点击的按键是回车键
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            //发送数据到Socket通道
            sendDataToSocket();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public static void main(String[] args) {
        new ServerMain();
    }
}

