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
@.inputFloat = private constant [18 x i8] c"Ingrese un Float:\00"
@.inputInteger = private constant [20 x i8] c"Ingrese un Integer:\00"
@.inputBool = private constant [26 x i8] c"Ingrese un Bool(0=f/1=t):\00"
@.bool_read_format = unnamed_addr constant [3 x i8] c"%d\00"
@.int_read_format = unnamed_addr constant [3 x i8] c"%d\00"
@.double_read_format = unnamed_addr constant [4 x i8] c"%lf\00"


define i32 @main(i32, i8**) {
  %t$1 = alloca i1 ; alloca = %t$1
  store i1 0, i1* %t$1 ; %t$1 = 0
  %t$2 = alloca i32 ; alloca = %t$2
  store i32 50, i32* %t$2 ; %t$2 = 50
  br label %label$3

  label$3:
  %t$6 = load i32, i32* %t$2 ; %t$6 = ju
  %t$7 = icmp ne i32 %t$6, 55
  br i1 %t$7, label %label$4, label %label$5

  label$4:
  %t$9 = load i32, i32* %t$2 ; %t$9 = ju
  %t$10 = icmp slt i32 5, %t$9
  br i1 %t$10, label %label$8, label %label$5

  label$8:
  %t$11 = load i1, i1* %t$1 ; %t$11 = zz
  store i1 %t$11, i1* %t$1 ; %t$1 = %t$11
  br label %label$3

  label$5:
  ret i32 0
}
