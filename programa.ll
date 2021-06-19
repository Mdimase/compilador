source_filename = "programa.ll"
target datalayout = "e-m:w-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-windows-msvc19.29.30038"

declare i32 @puts(i8*)
declare i32 @printf(i8*, ...)
declare i32 @scanf(i8* %0, ...)

@.true = private constant[4 x i8] c".T.\00"
@.false = private constant[4 x i8] c".F.\00"
@.integer = private constant [3 x i8] c"%d\00"
@.float = private constant [3 x i8] c"%f\00"
@.integern = private constant [4 x i8] c"%d\0A\00"
@.floatn = private constant [4 x i8] c"%f\0A\00"
@int_read_format = unnamed_addr constant [3 x i8] c"%d\00"
@double_read_format = unnamed_addr constant [4 x i8] c"%lf\00"

@d = global i1 0
@hj = global i32 0
@rt = global float 0.0
@bv = global i1 0
@fghjl = global i1 0
@jk = global float 0.0
@df = global i32 0
@str.31 = private constant [6 x i8] c"soy t\00"
@str.33 = private constant [6 x i8] c"soy f\00"

define i32 @hola (float %t$8,i32 %t$9) {
  %t$10 = alloca float ; alloca = %t$10
  store float %t$8, float* %t$10 ; %t$10 = %t$8
  %t$11 = alloca i32 ; alloca = %t$11
  store i32 %t$9, i32* %t$11 ; %t$11 = %t$9
  %t$12 = load float, float* %t$10 ; %t$12 = g21
  %t$13 = fptosi float %t$12 to i32
  ret i32 %t$13
}

define i32 @main(i32, i8**) {
  store i1 0, i1* @d ; @d = 0
  store i32 66, i32* @hj ; @hj = 66
  store float 5.559999942779541, float* @rt ; @rt = 5.559999942779541
  %t$1 = load float, float* @rt ; %t$1 = rt
  %t$2 = fcmp oeq float %t$1, 5.0
  store i1 %t$2, i1* @bv ; @bv = %t$2
  %t$3 = load i1, i1* @d ; %t$3 = d
  %t$4 = or i1 %t$3, 1
  store i1 %t$4, i1* @fghjl ; @fghjl = %t$4
  store float 0.0, float* @jk ; @jk = 0.0
  %t$5 = load float, float* @rt ; %t$5 = rt
  %t$6 = fadd float 1.0, %t$5
  %t$7 = fptosi float %t$6 to i32
  store i32 %t$7, i32* @df ; @df = %t$7
  %t$14 = alloca i32 ; alloca = %t$14
  store i32 50, i32* %t$14 ; %t$14 = 50
  %t$15 = alloca float ; alloca = %t$15
  %t$16 = fneg float 1.2300000190734863
  store float %t$16, float* %t$15 ; %t$15 = %t$16
  %t$17 = alloca i1 ; alloca = %t$17
  store i1 0, i1* %t$17 ; %t$17 = 0
  %t$18 = alloca i32 ; alloca = %t$18
  %t$19 = load i32, i32* %t$14 ; %t$19 = j
  %t$20 = add i32 %t$19, 500
  store i32 %t$20, i32* %t$18 ; %t$18 = %t$20
  %t$21 = load i32, i32* %t$18 ; %t$21 = int
  %t$22 = add i32 %t$21, 1
  store i32 %t$22, i32* %t$18 ; %t$18 = %t$22
  %t$27 = load i32, i32* %t$14 ; %t$27 = j
  %t$28 = icmp eq i32 %t$27, 56
  br i1 %t$28, label %label$26, label %label$24

  label$26:
  %t$29 = load i32, i32* %t$18 ; %t$29 = int
  %t$30 = icmp slt i32 %t$29, 100
  br i1 %t$30, label %label$23, label %label$24

  label$23:
  %t$32 = call i32 @puts(i8* getelementptr ([6 x i8], [6 x i8] * @str.31, i32 0, i32 0))
  br label %label$25

  label$24:
  %t$34 = call i32 @puts(i8* getelementptr ([6 x i8], [6 x i8] * @str.33, i32 0, i32 0))
  br label %label$25

  label$25:
ret i32 0
}
