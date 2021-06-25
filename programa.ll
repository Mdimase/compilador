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

@.9 = global i1 0
@.15 = global i32 0
@.4 = global i32 0
@.5 = global float 0.0
@.16 = global i1 0
@.10 = global i1 0
@.1 = global float 0.0
@.6 = global i32 0
@.14 = global i32 0
@.7 = global i32 0
@.2 = global float 0.0
@.8 = global i1 0
@.20 = global i32 0
@.17 = global i32 0
@.12 = global i1 0
@.3 = global i1 0
@.21 = global float 0.0
@str.22 = private constant [18 x i8] c"Estoy en el else\0A\00"
@str.95 = private constant [8 x i8] c"daleee\0A\00"
@str.112 = private constant [12 x i8] c"j>100.23+j\0A\00"
@str.119 = private constant [10 x i8] c"j<253.98\0A\00"
@str.121 = private constant [6 x i8] c"sino\0A\00"
@str.131 = private constant [7 x i8] c"soy t\0A\00"
@str.133 = private constant [7 x i8] c"soy f\0A\00"
@str.141 = private constant [6 x i8] c"pepe\0A\00"
@str.145 = private constant [9 x i8] c"no sale\0A\00"

define i32 @.11 (i32 %t$8) {
  %t$9 = alloca i32 ; alloca = %t$9
  store i32 %t$8, i32* %t$9 ; %t$9 = %t$8
  %t$13 = load i32, i32* @.15 ; %t$13 = entero?
  %t$14 = icmp sge i32 %t$13, 10
  br i1 %t$14, label %label$10, label %label$11

  label$10:
  %t$17 = load i32, i32* @.4 ; %t$17 = hj
  %t$18 = icmp eq i32 %t$17, 66
  br i1 %t$18, label %label$15, label %label$16

  label$15:
  %t$19 = load i32, i32* %t$9 ; %t$19 = a
  %t$20 = sdiv i32 %t$19, 2
  %t$21 = call i32 (i8*, ...) @printf(i8* getelementptr([4 x i8], [4 x i8]* @.integern, i32 0, i32 0), i32 %t$20)
  br label %label$16

  label$16:
  br label %label$12

  label$11:
  %t$23 = call i32 (i8*, ...) @printf(i8* getelementptr([18 x i8], [18 x i8]* @str.22, i32 0, i32 0))
  br label %label$12

  label$12:
  %t$24 = load i32, i32* %t$9 ; %t$24 = a
  ret i32 %t$24
}

define i32 @.19 (float %t$25) {
  %t$26 = alloca float ; alloca = %t$26
  store float %t$25, float* %t$26 ; %t$26 = %t$25
  %t$28 = load float, float* %t$26 ; %t$28 = a
  %t$29 = fadd float %t$28, 8.0
  %t$30 = fptosi float %t$29 to i32
  %t$27 = call float @.18(i32 %t$30 )
  %t$31 = alloca i32 ; alloca = %t$31
  store i32 4000, i32* %t$31 ; %t$31 = 4000
  %t$32 = load i32, i32* %t$31 ; %t$32 = aux
  %t$33 = load i32, i32* @.14 ; %t$33 = p
  %t$34 = add i32 %t$32, %t$33
  %t$35 = call i32 (i8*, ...) @printf(i8* getelementptr([4 x i8], [4 x i8]* @.integern, i32 0, i32 0), i32 %t$34)
  %t$39 = load i32, i32* %t$31 ; %t$39 = aux
  %t$40 = icmp sgt i32 %t$39, 2
  br i1 %t$40, label %label$36, label %label$37

  label$36:
  %t$41 = alloca i1 ; alloca = %t$41
  store i1 0, i1* %t$41 ; %t$41 = 0
  br label %label$38

  label$37:
  %t$43 = load float, float* %t$26 ; %t$43 = a
  %t$44 = fadd float %t$43, 5.0
  %t$45 = fptosi float %t$44 to i32
  %t$42 = call float @.18(i32 %t$45 )
  br label %label$38

  label$38:
  ret i32 0
}

define float @.18 (i32 %t$46) {
  %t$47 = alloca i32 ; alloca = %t$47
  store i32 %t$46, i32* %t$47 ; %t$47 = %t$46
  br label %label$48

  label$48:
  %t$51 = load i32, i32* %t$47 ; %t$51 = z
  %t$52 = icmp slt i32 %t$51, 12
  br i1 %t$52, label %label$49, label %label$50

  label$49:
  %t$53 = load i32, i32* %t$47 ; %t$53 = z
  %t$54 = call i32 (i8*, ...) @printf(i8* getelementptr([4 x i8], [4 x i8]* @.integern, i32 0, i32 0), i32 %t$53)
  %t$55 = load i32, i32* %t$47 ; %t$55 = z
  %t$56 = add i32 %t$55, 1
  store i32 %t$56, i32* %t$47 ; %t$47 = %t$56
  br label %label$48

  label$50:
  ret float 0.0
}

define i1 @.13 (i32 %t$70,float %t$71,i1 %t$72) {
  %t$73 = alloca i32 ; alloca = %t$73
  store i32 %t$70, i32* %t$73 ; %t$73 = %t$70
  %t$74 = alloca float ; alloca = %t$74
  store float %t$71, float* %t$74 ; %t$74 = %t$71
  %t$75 = alloca i1 ; alloca = %t$75
  store i1 %t$72, i1* %t$75 ; %t$75 = %t$72
  store i1 0, i1* %t$75 ; %t$75 = 0
  %t$76 = load i1, i1* %t$75 ; %t$76 = skt
  ret i1 %t$76
}

define i32 @main(i32, i8**) {
  store i1 0, i1* @.9 ; @.9 = 0
  store i32 12, i32* @.15 ; @.15 = 12
  store i32 66, i32* @.4 ; @.4 = 66
  store float 5.559999942779541, float* @.5 ; @.5 = 5.559999942779541
  %t$1 = load float, float* @.5 ; %t$1 = rt
  %t$2 = fcmp oeq float %t$1, 5.0
  store i1 %t$2, i1* @.16 ; @.16 = %t$2
  %t$3 = load i1, i1* @.9 ; %t$3 = d
  %t$4 = or i1 %t$3, 1
  store i1 %t$4, i1* @.10 ; @.10 = %t$4
  store float 0.0, float* @.1 ; @.1 = 0.0
  %t$5 = load float, float* @.5 ; %t$5 = rt
  %t$6 = fadd float 1.0, %t$5
  %t$7 = fptosi float %t$6 to i32
  store i32 %t$7, i32* @.6 ; @.6 = %t$7
  %t$57 = load i32, i32* @.4 ; %t$57 = hj
  %t$58 = add i32 15, %t$57
  store i32 %t$58, i32* @.14 ; @.14 = %t$58
  store i32 100, i32* @.7 ; @.7 = 100
  %t$59 = load float, float* @.5 ; %t$59 = rt
  %t$60 = load float, float* @.1 ; %t$60 = jk
  %t$61 = fsub float %t$59, %t$60
  store float %t$61, float* @.2 ; @.2 = %t$61
  store i1 0, i1* @.8 ; @.8 = 0
  store i32 560, i32* @.20 ; @.20 = 560
  store i32 0, i32* @.17 ; @.17 = 0
  %t$62 = load i1, i1* @.9 ; %t$62 = d
  %t$63 = xor i1 %t$62, 1
  store i1 %t$63, i1* @.12 ; @.12 = %t$63
  %t$64 = load i32, i32* @.4 ; %t$64 = hj
  %t$65 = load i32, i32* @.6 ; %t$65 = df
  %t$66 = icmp ne i32 %t$64, %t$65
  store i1 %t$66, i1* @.3 ; @.3 = %t$66
  %t$67 = load i32, i32* @.20 ; %t$67 = nm
  %t$68 = add i32 12, %t$67
  %t$69 = sitofp i32 %t$68 to float
  store float %t$69, float* @.21 ; @.21 = %t$69
  %t$77 = alloca i32 ; alloca = %t$77
  store i32 50, i32* %t$77 ; %t$77 = 50
  %t$78 = load i32, i32* %t$77 ; %t$78 = j
  %t$79 = add i32 %t$78, 120
  %t$80 = sub i32 %t$79, 20
  store i32 %t$80, i32* %t$77 ; %t$77 = %t$80
  %t$81 = alloca float ; alloca = %t$81
  %t$82 = fneg float 1.2300000190734863
  store float %t$82, float* %t$81 ; %t$81 = %t$82
  %t$83 = alloca i1 ; alloca = %t$83
  store i1 0, i1* %t$83 ; %t$83 = 0
  %t$84 = alloca i32 ; alloca = %t$84
  %t$85 = load i32, i32* %t$77 ; %t$85 = j
  %t$86 = add i32 %t$85, 500
  store i32 %t$86, i32* %t$84 ; %t$84 = %t$86
  %t$87 = load i32, i32* %t$84 ; %t$87 = int
  %t$88 = add i32 %t$87, 1
  store i32 %t$88, i32* %t$84 ; %t$84 = %t$88
  %t$89 = alloca i32 ; alloca = %t$89
  store i32 6, i32* %t$89 ; %t$89 = 6
  br label %label$90

  label$90:
  %t$93 = load i32, i32* %t$89 ; %t$93 = t8
  %t$94 = icmp sle i32 1, %t$93
  br i1 %t$94, label %label$91, label %label$92

  label$91:
  %t$96 = call i32 (i8*, ...) @printf(i8* getelementptr([8 x i8], [8 x i8]* @str.95, i32 0, i32 0))
  %t$97 = load i32, i32* %t$89 ; %t$97 = t8
  %t$98 = call i32 (i8*, ...) @printf(i8* getelementptr([4 x i8], [4 x i8]* @.integern, i32 0, i32 0), i32 %t$97)
  %t$99 = load i32, i32* %t$89 ; %t$99 = t8
  %t$100 = sub i32 %t$99, 2
  store i32 %t$100, i32* %t$89 ; %t$89 = %t$100
  br label %label$90

  label$92:
  %t$101 = alloca i32 ; alloca = %t$101
  %t$102 = load i32, i32* %t$77 ; %t$102 = j
  store i32 %t$102, i32* %t$101 ; %t$101 = %t$102
  %t$106 = load i32, i32* %t$77 ; %t$106 = j
  %t$107 = load i32, i32* %t$77 ; %t$107 = j
  %t$108 = sitofp i32 %t$107 to float
  %t$109 = fadd float 100.2300033569336, %t$108
  %t$110 = fptosi float %t$109 to i32
  %t$111 = icmp sgt i32 %t$106, %t$110
  br i1 %t$111, label %label$103, label %label$104

  label$103:
  %t$113 = call i32 (i8*, ...) @printf(i8* getelementptr([12 x i8], [12 x i8]* @str.112, i32 0, i32 0))
  br label %label$105

  label$104:
  %t$117 = load i32, i32* %t$77 ; %t$117 = j
  %t$118 = icmp slt i32 %t$117, 253
  br i1 %t$118, label %label$114, label %label$115

  label$114:
  %t$120 = call i32 (i8*, ...) @printf(i8* getelementptr([10 x i8], [10 x i8]* @str.119, i32 0, i32 0))
  br label %label$116

  label$115:
  %t$122 = call i32 (i8*, ...) @printf(i8* getelementptr([6 x i8], [6 x i8]* @str.121, i32 0, i32 0))
  br label %label$116

  label$116:
  br label %label$105

  label$105:
  %t$127 = load i32, i32* %t$77 ; %t$127 = j
  %t$128 = icmp eq i32 %t$127, 56
  br i1 %t$128, label %label$126, label %label$124

  label$126:
  %t$129 = load i32, i32* %t$84 ; %t$129 = int
  %t$130 = icmp slt i32 %t$129, 100
  br i1 %t$130, label %label$123, label %label$124

  label$123:
  %t$132 = call i32 (i8*, ...) @printf(i8* getelementptr([7 x i8], [7 x i8]* @str.131, i32 0, i32 0))
  br label %label$125

  label$124:
  %t$134 = call i32 (i8*, ...) @printf(i8* getelementptr([7 x i8], [7 x i8]* @str.133, i32 0, i32 0))
  br label %label$125

  label$125:
  %t$135 = alloca i32 ; alloca = %t$135
  store i32 50, i32* %t$135 ; %t$135 = 50
  br label %label$136

  label$136:
  %t$139 = load i32, i32* %t$135 ; %t$139 = ju
  %t$140 = icmp ne i32 %t$139, 55
  br i1 %t$140, label %label$137, label %label$138

  label$137:
  %t$142 = call i32 (i8*, ...) @printf(i8* getelementptr([6 x i8], [6 x i8]* @str.141, i32 0, i32 0))
  %t$143 = load i32, i32* %t$135 ; %t$143 = ju
  %t$144 = add i32 %t$143, 1
  store i32 %t$144, i32* %t$135 ; %t$135 = %t$144
  br label %label$138  %t$146 = call i32 (i8*, ...) @printf(i8* getelementptr([9 x i8], [9 x i8]* @str.145, i32 0, i32 0))
  br label %label$136

  label$138:
  %t$147 = call i1 @.13(i32 10, float 50.0 , i1 1)
  %t$148 = zext i1 %t$147 to i32
  %t$149 = call i32 (i8*, ...) @printf(i8* getelementptr([4 x i8], [4 x i8]* @.integern, i32 0, i32 0), i32 %t$148)
  %t$150 = alloca i1 ; alloca = %t$150
  %t$151 = alloca i1 ; alloca = %t$151
  %t$152 = call i32 (i8*, ...) @printf(i8* getelementptr([26 x i8], [26 x i8]* @.inputBool, i32 0, i32 0))
  %t$153 = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.bool_read_format, i32 0, i32 0), i1* %t$151)
  %t$154 = load i1, i1* %t$151 ; %t$154 = %t$151
  store i1 %t$154, i1* %t$150 ; %t$150 = %t$154
  %t$155 = load i1, i1* %t$150 ; %t$155 = vbn
  %t$156 = zext i1 %t$155 to i32
  %t$157 = call i32 (i8*, ...) @printf(i8* getelementptr([4 x i8], [4 x i8]* @.integern, i32 0, i32 0), i32 %t$156)
  %t$158 = alloca i32 ; alloca = %t$158
  %t$159 = alloca i32 ; alloca = %t$159
  %t$160 = call i32 (i8*, ...) @printf(i8* getelementptr([20 x i8], [20 x i8]* @.inputInteger, i32 0, i32 0))
  %t$161 = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.int_read_format, i64 0, i64 0), i32* %t$159)
  %t$162 = load i32, i32* %t$159 ; %t$162 = %t$159
  store i32 %t$162, i32* %t$158 ; %t$158 = %t$162
  %t$164 = load i32, i32* %t$158 ; %t$164 = aw
  %t$163 = call i32 @.11(i32 %t$164 )
  ret i32 0
}
