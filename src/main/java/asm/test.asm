#参数、调用位置、返回值,递归执行的三个步骤
#开始递归函数调用
addiu $sp, $0, 0x10010080
#压栈入参
addiu $s0, $0, 5 # n=5
#把s0寄存器里面的值放到栈里面，这个算是初始化一下栈，
sw $s0, 0($sp)
#这里是压栈
addiu $sp, $sp, -4
jal FACT
nop
j END
nop
FACT:
#压栈返回地址
sw $ra, 0($sp)
addiu $sp, $sp, -4
#读取入参
lw $s0,8($sp)
#压栈返回值
sw $0,0($sp)
addiu $sp,$sp,-4
#递归base条件
#if(n==0){return 1}
bne $s0,$0,RECURSION
nop
#读取返回地址，下面jr指令要用到，
lw $t1,8($sp)

#出栈：返回值，返回地址，因为有这两个，返回值返回地址，所以要加8
addiu $sp, $sp, 8
#压栈返回值，就是说这里没有跳到RECURTION，继续往下走到这里了，走到这里了，那就是return 1 了
addiu $s0, $zero, 1
#你这里返回1，就是作为结果给返回回去了，那你这个结果肯定也是要作为参数入栈的，所以这里要入栈
sw $s0, 0($sp)
addiu $sp,$sp,-4



jr $t1
nop
RECURSION: #recurtion
# return fact(n-1) * n  递归就是你得先一直进入到return 1的那个位置，下面的都算完了，
#才能算出最前面的那个，所以，继续跳到FACT

#这个就是n-1里面的那个n-1
addiu $s1, $s0, -1
sw $s1, 0($sp)
addiu $sp, $sp, -4

jal FACT
nop

#现在的栈是什么样子？  参数|返回地址|返回值|子递归的参数|子函数的返回值(因为最后一直都是压栈返回值)|当前SP
#当前参数,当前sp到参数，有5个，那么就5 * 4 = 20
lw $s0,20($sp)
#子函数返回值
lw $s1,4($sp)
#返回地址
lw $t1,16($sp)

#乘法
mult $s1,$s0
mflo $s2


#出栈（返回地址|返回值|子递归的参数|子函数的返回值）这些是要清理的
addiu $sp,$sp,16
#返回值压栈
sw $s2,0($sp)
addiu $sp,$sp,-4
jr $t1
END: