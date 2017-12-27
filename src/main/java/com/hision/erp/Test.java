package com.hision.erp;

public class Test {
	public static void main(String[] args) {
		
		int a = 6; // 货架
		int b = 6; // 列数
		
		System.out.println(a%b);
		
		// if (a<=b) => row = 1
		// if (a>b && a%b == 0) => row = a/b
		// if (a>b && a%b !=0) => row =a/b + 1
		
	}
}
