/**
 * @author CS415
 */

package a2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@ExtendWith(MockitoExtension.class)
public class TestChatbotServerPartial {

  @Mock
  public Chatbot mockChatbot;

  @Mock
  public ServerSocket mockServerSocket;

  @Mock
  public Socket mockSocket;

  public ChatbotServerPartial myServer;

  @BeforeEach
  public void setUp() {
    myServer = new ChatbotServerPartial(mockChatbot, mockServerSocket);
  }

  // @Test
  // public void testOutput() throws Exception {
  // when(mockServerSocket.accept()).thenReturn(mockSocket);
  // OutputStream myOutputStream = new ByteArrayOutputStream();
  // when(mockSocket.getOutputStream()).thenReturn(myOutputStream);
  //
  // myServer.handleOneClient();
  //
  // assertEquals("Output\n", myOutputStream.toString());
  // }

  @Test
  public void testHappyPathOneLine() throws Exception {
    when(mockServerSocket.accept()).thenReturn(mockSocket);

    InputStream s = new ByteArrayInputStream("GameBot\n".getBytes());
    when(mockSocket.getInputStream()).thenReturn(s);

    OutputStream myOutputStream = new ByteArrayOutputStream();
    when(mockSocket.getOutputStream()).thenReturn(myOutputStream);

    // mock the behavior of ChatBot

    myServer.handleOneClient();

    assertEquals("GameBot response\n", myOutputStream.toString());

  }
}
