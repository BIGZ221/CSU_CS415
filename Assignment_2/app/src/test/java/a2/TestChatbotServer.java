/**
 * @author Zachary Fuchs
 */

package a2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.Spy;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@ExtendWith(MockitoExtension.class)
public class TestChatbotServer {

  private String newLine = System.lineSeparator();

  @Mock
  public Chatbot mockChatbot;

  @Mock
  public ServerSocket mockServerSocket;

  @Mock
  public Socket mockSocket;

  @Spy
  public IOException mockIoException;

  public ChatbotServer myServer;

  @BeforeEach
  public void setUp() {
    myServer = new ChatbotServer(mockChatbot, mockServerSocket);
  }

  @Test
  public void testHappyPathOneLine() throws Exception {
    when(mockServerSocket.accept()).thenReturn(mockSocket);
    String input = "GameBot";
    String expected = "good resp";
    InputStream s = new ByteArrayInputStream(input.getBytes());
    when(mockSocket.getInputStream()).thenReturn(s);

    OutputStream myOutputStream = new ByteArrayOutputStream();
    when(mockSocket.getOutputStream()).thenReturn(myOutputStream);
    when(mockChatbot.getResponse(input)).thenReturn(expected);
    myServer.handleOneClient();
    assertEquals(expected + newLine, myOutputStream.toString());
  }

  @Test
  public void aiExceptionSendsCorrectOutput() throws Exception {
    String input = "nun";
    String errorMessage = "Unknown sentence";
    String expected = "Got AIException: " + errorMessage + newLine;
    when(mockServerSocket.accept()).thenReturn(mockSocket);
    InputStream iStream = new ByteArrayInputStream(input.getBytes());
    when(mockSocket.getInputStream()).thenReturn(iStream);
    OutputStream oStream = new ByteArrayOutputStream();
    when(mockSocket.getOutputStream()).thenReturn(oStream);
    when(mockChatbot.getResponse(input)).thenThrow(new AIException(errorMessage));
    myServer.handleOneClient();
    assertEquals(expected, oStream.toString());
  }

  @Test
  public void multipleRequestsReturnCorrectOutput() throws Exception {
    String word = "hello";
    String input = word + newLine + word + newLine + word;
    String expected = "1" + newLine + "2" + newLine + "3";
    when(mockServerSocket.accept()).thenReturn(mockSocket);
    InputStream iStream = new ByteArrayInputStream(input.getBytes());
    when(mockSocket.getInputStream()).thenReturn(iStream);
    OutputStream oStream = new ByteArrayOutputStream();
    when(mockSocket.getOutputStream()).thenReturn(oStream);
    when(mockChatbot.getResponse(word)).thenReturn("1", "2", "3");
    myServer.handleOneClient();
    assertEquals(expected + newLine, oStream.toString());
  }

  @Test
  public void errorThenValidRequestReturnCorrect() throws Exception {
    String word = "hello";
    String input = word + newLine + word;
    String expected = "Got AIException: oops" + newLine + "hey" + newLine;
    when(mockServerSocket.accept()).thenReturn(mockSocket);
    InputStream iStream = new ByteArrayInputStream(input.getBytes());
    when(mockSocket.getInputStream()).thenReturn(iStream);
    OutputStream oStream = new ByteArrayOutputStream();
    when(mockSocket.getOutputStream()).thenReturn(oStream);
    when(mockChatbot.getResponse(word)).thenThrow(new AIException("oops")).thenReturn("hey");
    myServer.handleOneClient();
    assertEquals(expected, oStream.toString());
  }

  @Test
  public void allErrorsReturnCorrect() throws Exception {
    String err = "h";
    String input = err + newLine + err + newLine + err;
    String expected = "Got AIException: oops" + newLine + "Got AIException: oops" + newLine
        + "Got AIException: oops" + newLine;
    when(mockServerSocket.accept()).thenReturn(mockSocket);
    InputStream iStream = new ByteArrayInputStream(input.getBytes());
    when(mockSocket.getInputStream()).thenReturn(iStream);
    OutputStream oStream = new ByteArrayOutputStream();
    when(mockSocket.getOutputStream()).thenReturn(oStream);
    when(mockChatbot.getResponse(err)).thenThrow(new AIException("oops"));
    myServer.handleOneClient();
    assertEquals(expected, oStream.toString());
  }

  @Test
  public void validErrorValidReturnsCorrectString() throws Exception {
    String err = "tt";
    String word = "hello";
    String input = word + newLine + err + newLine + word;
    String expected = "hey" + newLine + "Got AIException: oops" + newLine + "hey" + newLine;
    when(mockServerSocket.accept()).thenReturn(mockSocket);
    InputStream iStream = new ByteArrayInputStream(input.getBytes());
    when(mockSocket.getInputStream()).thenReturn(iStream);
    OutputStream oStream = new ByteArrayOutputStream();
    when(mockSocket.getOutputStream()).thenReturn(oStream);
    when(mockChatbot.getResponse(err)).thenThrow(new AIException("oops"));
    when(mockChatbot.getResponse(word)).thenReturn("hey");
    myServer.handleOneClient();
    assertEquals(expected, oStream.toString());
  }

  @Test
  public void gettingInputStreamExceptionPrintsStacktrace() throws Exception {
    when(mockServerSocket.accept()).thenReturn(mockSocket);
    when(mockSocket.getInputStream()).thenThrow(mockIoException);
    myServer.handleOneClient();
    verify(mockIoException).printStackTrace();
  }

  @Test
  public void gettingOutputStreamExceptionPrintsStacktrace() throws Exception {
    when(mockServerSocket.accept()).thenReturn(mockSocket);
    InputStream iStream = new ByteArrayInputStream("t".getBytes());
    when(mockSocket.getInputStream()).thenReturn(iStream);
    when(mockSocket.getOutputStream()).thenThrow(mockIoException);
    myServer.handleOneClient();
    verify(mockIoException).printStackTrace();
  }

}
