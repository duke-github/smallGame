package test;

import java.util.*;

public class Leetcode {


    static class Node {
        int key;
        int value;
        Node next;
        Node before;
    }

    static class LRUCache {
        int size;
        Node head = new Node();
        Node tail = new Node();


        {
            head.next = tail;
            tail.before = head;
        }

        Map<Integer, Node> map = new HashMap<>();

        public LRUCache(int capacity) {
            size = capacity;
        }

        public int get(int key) {
            Node node = map.get(key);
            if (node == null) {
                return -1;
            }
            switchToHead(node);
            return node.value;
        }

        public void put(int key, int value) {
            Node node = map.getOrDefault(key, new Node());
            node.value = value;
            node.key = key;
            switchToHead(node);
            map.put(key, node);
            if (map.size() > size) {
                //获取尾部第一个有效节点
                Node temp = tail.before;
                tail.before = temp.before;
                temp.before.next = tail;
                map.remove(temp.key);
            }
        }


        void switchToHead(Node node) {
            if (node == head.next) {
                return;
            }
            if (node.before != null) {
                node.before.next = node.next;
            }
            if (node.next != null) {
                node.next.before = node.before;
            }
            head.next.before = node;
            node.next = head.next;
            node.before = head;
            head.next = node;

        }
    }


    public static int[] maxSlidingWindow(int[] nums, int k) {
        PriorityQueue<Integer> queue = new PriorityQueue<>(k, Integer::compareTo);
        int[] ans = new int[nums.length - k + 1];
        for (int i = 0; i < k; i++) {
            queue.offer(nums[i]);
        }
        for (int i = k; i < nums.length; i++) {
            ans[i - k] = queue.poll();
            queue.remove(nums[i - k]);
            queue.offer(nums[i]);
        }
        ans[nums.length - k] = queue.poll();
        return ans;
    }


    //对所有字母按照出现的次数排序
    //相同次数的先出现的在前面
    public static String sortVowels(String s) {
        Character[] vowels = new Character[]{'a', 'e', 'i', 'o', 'u'};
        List<Character> list = Arrays.asList(vowels);
        //记录出现的次数
        Map<Character, Integer> countMap = new HashMap<>();
        Map<Character, Integer> firstMap = new HashMap<>();
        for (char c : vowels) {
            countMap.put(c, 0);
        }

        char[] chars = s.toCharArray();
        int j = 0;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (list.contains(c)) {
                countMap.merge(c, 1, Integer::sum);
                if (!firstMap.containsKey(c)) {
                    firstMap.put(c, i);
                    j++;
                }
            }
        }

        list.sort((o1, o2) -> {
            if (Objects.equals(countMap.get(o1), countMap.get(o2))) {
                return firstMap.get(o1) - firstMap.get(o2);
            }
            return countMap.get(o2) - countMap.get(o1);
        });
        int k = 0;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (list.contains(c)) {
                Character c1 = list.get(k);
                while (countMap.get(c1) == 0) {
                    k++;
                    c1 = list.get(k);
                }
                chars[i] = c1;
                countMap.merge(c1, -1, Integer::sum);
            }
        }
        return new String(chars);
    }


    public static void main(String[] args) {
        minOperations(new int[]{10, 12, 4});
    }

    public static long minOperations(int[] nums) {
        int max = nums[0];
        int x = 0;
        for (int i : nums) {
            //对于每个数字  +前面执行的x 小于前面的数字 就必须加对应的x
            if (max > i + x) {
                x += i + x - max;
            } else {
                max = i + x;
            }
        }
        return x;
    }

    public static List<Integer> findValidElements(int[] nums) {
        List<Integer> ans = new ArrayList<>();
        boolean[] flag = new boolean[nums.length];
        int max = 0;
        for (int i = 0; i < nums.length; i++) {
            if (max < nums[i]) {
                max = nums[i];
                ans.add(nums[i]);
                flag[i] = true;
            }
        }
        max = 0;
        int j = 0;
        //从后依次加入
        for (int i = nums.length - 1; i > 0; i--) {
            if (max < nums[i]) {
                max = nums[i];
                if (!flag[i]) {
                    flag[i] = true;
                    ans.add(ans.size() - j, nums[i]);
                    j++;
                }
            }
        }
        return ans;

    }


    public int maxArea(int[] height) {
        int first = 0, second = height.length - 1;
        int max = 0;
        while (first < second) {
            max = Math.max((second - first) * Math.min(height[first], height[second]), max);
            if (height[first] >= height[second]) {
                second--;
            } else {
                first++;
            }
        }
        return max;
    }

    /**
     * 对于每个数字 加入map
     * 遍历数组 查找每个数字是否存在+1的数字 一直寻找到不存在下一个+1
     * ps:对于每个数字 都会遍历所有他能够+1达到的数据
     * 优化1:对于前面已经遍历过的 使用map对他们进行长度记录<数字,后续的长度>
     * 优化2:对于相同的数字 如果前面已经遍历过 直接跳过
     * 优化3:在对某一个数字遍历过程中遇到的其他的数据 直接对他进行赋值
     * 优化4:第一个map的value没使用 用set替换map 遍历也是用set
     */
    public int longestConsecutive(int[] nums) {
        //标记每个数字 对应的下标
        Set<Integer> set = new HashSet<>();
        for (int k : nums) {
            set.add(k);
        }
        int max = 0;

        for (int num : nums) {
            if (set.contains(num - 1)) {
                continue;
            }
            int count = 1, current = num;
            while (set.contains(current + 1)) {
                current++;
                count++;
            }
            max = Math.max(count, max);
        }
        return max;

    }

    public int maxSubArray(int[] nums) {
        int ans = Integer.MIN_VALUE, max = Integer.MIN_VALUE;
        int i = 0;
        for (; i < nums.length; i++) {
            if (nums[i] > max) {
                max = nums[i];
                ans = max;
                if (i > 0) {
                    //找到第一个大于0的数字
                    break;
                }
            }
        }

        for (; i < nums.length; i++) {
            if (nums[i] > 0) {
                System.out.println(ans);
                ans = Math.max(ans, max);
                max += nums[i];
            } else if (nums[i] + max > 0) {
                max += nums[i];
            } else {
                max = 0;
            }
        }
        return ans;


    }

    public void moveZeroes(int[] nums) {
        int low = 0, count = 0;

        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == 0) {
                if (count == 0) {
                    low = i;
                    count = 1;
                }
                //向后寻找第一个不为0的替换
                while (low < nums.length) {
                    if (nums[low] != 0) {
                        nums[i] = nums[low];
                        nums[low] = 0;
                        break;
                    }
                    low++;
                }
            }
        }
    }

    public static int lengthOfLongestSubstring(String s) {
        //滑动窗口  low fast 从0开始  答案是 (fast和low分别站在相同的字母上面)fast - low (fast和low分别站在不同的字母上面)fast - low +1
        int[] count = new int[128];
        int low = 0, fast = 0, ans = 0;
        char[] charArray = s.toCharArray();
        for (; fast < s.length(); fast++) {
            char c = charArray[fast];
            if (count[c] != 0) {
                while (low < fast) {
                    if (c == charArray[low]) {
                        //fast和low分别站在相同的字母上面
                        ans = Math.max(fast - low, ans);//1 3
                        //low再向后一位 去掉当前位置上的相同字母
                        low++;
                        break;
                    }
                    count[charArray[low]]--;
                    low++;
                }
            } else {
                //fast和low分别站在不同的字母上面
                ans = Math.max(fast - low + 1, ans);//1 2 1 2
                count[c]++;
            }
        }
        return ans;
    }
}

