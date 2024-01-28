**To run the app:**  
mvn compile  
mvn exec:java -Dexec.mainClass="networks.lab2.Main"  
**_______________________________**  

**The app aim** is to send and receive file using TCP.  
**The program works as follows:**  
Firstly, the user needs to choose the mode of working app:  
  * server (mode "s")  
  * client (mode "c")
  
Secondly, the user needs to enter the port.  
If it is the client mode, the user needs to enter server ip and the path to sending file.
Then the program starts the necessary threads depending on the mode.  
The Server is responsible for sending file. It expects information about the file being sent from the client, checks it, and if everything suits, allows the file to be sent and begins receiving it.  
The Client is responsible for receiving file from server. It first sends the file name size, file size and file name to the server, then waits for the server to agree to send and starts sending the file.

