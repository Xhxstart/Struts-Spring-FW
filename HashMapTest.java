package com.learning.demo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class HashMapTest {

	public static void main(String[] args) throws Exception {
		// TODO 自動生成されたメソッド・スタブ
		Map<String, String> map = new HashMap<String, String>();

		// 获取map扩容时临界阈值 阈值 = 容量 * 加载因子
		// 默认容量 为16，加载因子 默认为0.75
		Field threshold = map.getClass().getDeclaredField("threshold");
		Field size = map.getClass().getDeclaredField("size");
		Method capacity = map.getClass().getDeclaredMethod("capacity");
		// 获取属性的属性值并修改属性值,但是获取私有属性的时候必须先设置Accessible为true，然后才能获取
		threshold.setAccessible(true);
		size.setAccessible(true);
		capacity.setAccessible(true);

		// 未存放对象时，各项值测试
		System.out.println("start:临界值:" + threshold.get(map));
		System.out.println("start:size:" + size.get(map));
		System.out.println("start:容量:" + capacity.invoke(map));

		// 临界值、容量测试
		for (int i = 1; i < 26; i++) {
			map.put(String.valueOf(i), i + "**");
			if (i == 11 || i == 12 || i == 13 || i == 23 || i == 24 || i == 25) {
				System.out.println("第" + i + "个对象, size为" + size.get(map) + ", 临界值为" + threshold.get(map) + ", 容量为"
						+ capacity.invoke(map));
			}
		}
	}

}
