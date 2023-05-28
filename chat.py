import socket
import threading

server = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
server_ip = socket.gethostbyname(socket.gethostname())
server.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
server.bind((server_ip, 8080))

def receive():
    while True:
        try:
            message, addr = server.recvfrom(1024)
            ip, _ = addr
            if ip == server_ip:
                print(f'[you]: {message.decode()}')
            else:
                print(f'[{addr}]: {message.decode()}')
        except:
            pass

def broadcast(message):
    server.sendto(message.encode(), ('<broadcast>', 8080))

def whisper(address, message):
    server.sendto(message.encode(), (address, 8080))
    
def send():
    while True:
        message = input('> ')
        print("\033[A                             \033[A") #overwrite input line
        if (message.startswith('/w ')):
            while len(message.split(" ")) != 3:
                print("Incorrect input. Try again")
                message = input('> ')
            _, ip, msg = message.split(" ")
            whisper(ip, msg)
        else:
            broadcast(message)

receive_thread = threading.Thread(target=receive)
send_thread = threading.Thread(target=send)

receive_thread.start()
send_thread.start()