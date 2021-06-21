source_filename = "programa.ll"
target datalayout = "e-m:w-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-windows-msvc19.29.30038"

declare i32 @puts(i8*)
declare i32 @printf(i8*, ...)
declare i32 @scanf(i8* %0, ...)

@.bool = private constant [3 x i8] c"%d\00"
@.booln = private constant [4 x i8] c"%d\0A\00"
@.integer = private constant [3 x i8] c"%d\00"
@.float = private constant [3 x i8] c"%f\00"
@.integern = private constant [4 x i8] c"%d\0A\00"
@.floatn = private constant [4 x i8] c"%f\0A\00"
@.int_read_format = unnamed_addr constant [3 x i8] c"%d\00"
@.double_read_format = unnamed_addr constant [4 x i8] c"%lf\00"

@.1 = global i32 0
@.2 = global i32 0
@.4 = global i32 0
@str.10 = private constant [18 x i8] c"Estoy en el else\0A\00"

define i32 @.3 (i32 %t$1) {
  %t$2 = alloca i32 ; alloca = %t$2
  store i32 %t$1, i32* %t$2 ; %t$2 = %t$1
  %t$6 = load i32, i32* @.1 ; %t$6 = entero?
  %t$7 = icmp sge i32 %t$6, 10
  br i1 %t$7, label %label$3, label %label$4

  label$3:
  store i32 5, i32* %t$2 ; %t$2 = 5
  %t$8 = load i32, i32* %t$2 ; %t$8 = a
  %t$9 = call i32 (i8*, ...) @printf(i8* getelementptr([3 x i8], [3 x i8]* @.integer, i32 0, i32 0), i32 %t$8)
  br label %label$5

  label$4:
  %t$11 = call i32 (i8*, ...) @printf(i8* getelementptr([18 x i8], [18 x i8]* @str.10, i32 0, i32 0))
  br label %label$5

  label$5:
  %t$12 = load i32, i32* %t$2 ; %t$12 = a
  ret i32 %t$12
}

define i32 @main(i32, i8**) {
  store i32 12, i32* @.1 ; @.1 = 12
  store i32 66, i32* @.2 ; @.2 = 66
  %t$13 = load i32, i32* @.2 ; %t$13 = hj
  %t$14 = add i32 15, %t$13
  store i32 %t$14, i32* @.4 ; @.4 = %t$14
  %t$15 = call i32 @.3(i32 64 )
ret i32 0
}
