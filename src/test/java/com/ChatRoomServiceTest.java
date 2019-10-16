package com;

import edu.udacity.java.nano.WebSocketChatApplication;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest(classes = {WebSocketChatApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatRoomServiceTest {

    boolean isMacOs;

    @LocalServerPort
    private int randomServerPort;

    private WebDriver driver;
    private String url;



    @Before
    public void setup(){
        url="http://localhost:"+randomServerPort;
        System.out.println(url);
        String osName = System.getProperty("os.name").toLowerCase();
        isMacOs = osName.startsWith("mac os x");
        WebDriverManager.chromedriver().setup();
        driver= new ChromeDriver();
    }

    @Test
    public void test01chatLoginTest() throws  Exception{
        String name="Guven";
        loginToChat(name);
        String uname = driver.findElement(By.id("username")).getText();
        Assert.assertEquals(name, uname);
    }

    @Test
    public void test02sendMessageTest() throws Exception{
        loginToChat("user1");
        String message="test message";
        sendMessage(message);
        Thread.sleep(300);
        List<WebElement> webElements=driver.findElements(By.xpath("//div[@class='message-container']//div[contains(text(), '"+message+"')]"));
        if (webElements.size()==1) {
            String actualMessage = webElements.get(0).getText();
            Assert.assertTrue(actualMessage.contains("user1"));
            Assert.assertTrue(actualMessage.contains(message));
        }else{
            System.out.println("message is not send");
            Assert.assertTrue(false);
        }
    }

    @Test
    public void test03clearMessage() throws Exception{
        loginToChat("user1");
        String message="test message";
        sendMessage(message);
        List<WebElement> webElements=driver.findElements(By.xpath("//div[@class='message-container']//div[contains(text(), '"+message+"')]"));
        if (webElements.size()==1) {
            String actualMessage = webElements.get(0).getText();
            Assert.assertTrue(actualMessage.contains("user1"));
            Assert.assertTrue(actualMessage.contains(message));
        }else{
            System.out.println("message is not send");
            Assert.assertTrue(false);
        }
        driver.findElement(By.xpath("//button[contains(text(),'Clear')]")).click();
        webElements=driver.findElements(By.xpath("//div[@class='message-container']//div[contains(text(), '"+message+"')]"));
        Assert.assertEquals(webElements.size(),0);
    }

    @Test
    public void test04userCountTest() throws Exception{
        loginToChat("user1");
        String oldTab = driver.getWindowHandle();
        String a = "window.open('about:blank','_blank');";
        ((JavascriptExecutor)driver).executeScript(a);
        ArrayList<String> newTab = new ArrayList<>(driver.getWindowHandles());
        newTab.remove(oldTab);
        driver.switchTo().window(newTab.get(0));
        loginToChat("user2");
       int count=Integer.valueOf(driver.findElement(By.xpath("//*[.='Online Users']//following-sibling::span")).getText());
       Assert.assertEquals(2,count);
    }

    @Test
    public void test05MsgReceivedByAnotherUser() throws Exception{
        loginToChat("user1");
        String oldTab = driver.getWindowHandle();
        String a = "window.open('about:blank','_blank');";
        ((JavascriptExecutor)driver).executeScript(a);
        ArrayList<String> newTab = new ArrayList<>(driver.getWindowHandles());
        newTab.remove(oldTab);
        driver.switchTo().window(newTab.get(0));
        loginToChat("user2");
        driver.switchTo().window(oldTab);
        String message1="I do not recommend this course to learn spring boot";
        sendMessage(message1);
        driver.switchTo().window(newTab.get(0));
        List<WebElement> webElements=driver.findElements(By.xpath("//div[@class='message-container']//div[contains(text(), '"+message1+"')]"));
        if (webElements.size()==1) {
            String actualMessage = webElements.get(0).getText();
            Assert.assertTrue(actualMessage.contains("user1"));
            Assert.assertTrue(actualMessage.contains(message1));
        }else{
            System.out.println("message is not received");
            Assert.assertTrue(false);
        }
    }

    @Test
    public void test06UserLeavesChat() throws Exception{
        loginToChat("user1");
        driver.findElement(By.id("exit")).click();
        String expected= "Chat Room";
        String actual=driver.findElement(By.tagName("h3")).getText();
        Assert.assertEquals(expected,actual);
    }




    @After
    public void tearDown(){
        driver.close();
    }


    public void loginToChat(String userName) throws Exception {
        driver.navigate().to(url);
        driver.findElement(By.id("username")).sendKeys(userName);
        driver.findElement(By.xpath("//a[.='Login']")).click();
    }

    public void sendMessage(String message) throws  Exception{
        Thread.sleep(100);
        driver.findElement(By.id("msg")).sendKeys(message);
        String keysPressed;
        if (isMacOs) {
            keysPressed = Keys.chord(Keys.COMMAND, Keys.RETURN);
        }else{
            keysPressed = Keys.chord(Keys.CONTROL, Keys.RETURN);

        }
        Thread.sleep(100);
        driver.findElement(By.id("msg")).sendKeys(keysPressed);
    }
}
