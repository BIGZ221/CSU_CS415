package a2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The partial version only connects to a client and sends a response to a
 * single
 * message that comes from a client.
 * 
 * @author CS415
 *         You will need to
 *         1. handle multiple strings
 *         2. call getResponse on the mock Chatbot and use that response in the
 *         outputstream
 *         3. handle exceptions- AIException, IOException
 */
public class ChatbotServerPartial {

  /**
   * Reference to the AI Implementation of Chatbot.
   */
  private Chatbot chatbot;

  /**
   * Reference to the ServerSocket.
   */
  private ServerSocket serversocket;

  /**
   * Constructor for ChatbotServer.
   * 
   * @param chatbot      The chatbot to use.
   * @param serversocket The pre-configured ServerSocket to use.
   */
  public ChatbotServerPartial(Chatbot chatbot, ServerSocket serversocket) {
    this.chatbot = chatbot;
    this.serversocket = serversocket;
  }

  /**
   * Start the Chatbot server. Does not return.
   */
  public void startServer() {
    while (true)
      handleOneClient();
  }

  /**
   * Handle interaction with a single client. See assignment description.
   */
  //
  // public void handleOneClient() { // TODO:
  //
  //
  // }
  //

  // public void handleOneClient() { // TODO:
  // try {
  // Socket s = serversocket.accept();
  // PrintWriter out = new PrintWriter(s.getOutputStream(), true);
  // out.println("Output");
  // } catch (IOException e) {
  // e.printStackTrace();
  // }
  // }

  public void handleOneClient() { // TODO:
    try {
      Socket s = serversocket.accept();
      BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
      PrintWriter out = new PrintWriter(s.getOutputStream(), true);
      String input = in.readLine();
      out.println(input + " response");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
