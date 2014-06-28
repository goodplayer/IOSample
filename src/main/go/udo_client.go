package main

import (
	"fmt"
	"net"
)

func main() {
	addr, err := net.ResolveUDPAddr("udp", ":12345")
	fmt.Println(err)
	conn, err := net.DialUDP("udp", nil, addr)
	fmt.Println(err)
	buf := make([]byte, 10)
	buf[0] = byte(1)
	buf[1] = byte(2)
	for i := 0; i < 9; i++ {
		n, err := conn.Write(buf)
		fmt.Println(n, err)
	}
}
