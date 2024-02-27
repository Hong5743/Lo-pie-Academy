# Lo-pie-Academy
>Lo-Pie-Academy는 HRD-NET에서 수강생 등록을 해서 교육을 들었던 저희 팀원들의 경험으로 만든 온라인 수강 학원입니다.<br>
Lo-Pie-Academy LMS는 시간과 장소에 구애받지 않고 학습할 수 있는 온라인 수강 환경을 제공하여 근로자, 학생, 교사들에게 장소에 대한 부담을 줄이자는 마음으로 구현하였습니다.

## 1. 프로젝트 개요
* 프로젝트 기간: 2023.10.25 ~ 2023.11.25   
* 개발 인원:  5명
* 프로젝트 목적: 협업능력 향상 및 API 사용법 숙달

## 2. 기술 스택
Back: JAVA, SPRING BOOT, MYBATIS, MYSQL<br>
Front: HTML, CSS3, JS<br>
협업: [Git-hub](https://github.com/Jlostcode/LPuniv), [Notion](https://www.notion.so/Lo-Pie-6af789c9063843fd8fbc2669c6278372)<br>

## 3. 프로젝트 산춘물

<details>
<summary>[요구사항 정의서](https://docs.google.com/spreadsheets/d/18fmBbhwZKClWZBWMEcxrZC0NJB8ZXZBi/edit#gid=482328230)</summary>
<div markdown="1">
<img src="https://github.com/Hong5743/Lo-pie-Academy/assets/136396772/6a74ecb6-8845-4c6e-ac67-eb08af8d02e2" width="600" height="400" alt="요구사항 정의서"/>
</div>
</details>
[ERD](https://www.erdcloud.com/d/87t6dhQJQXXFwyN98)
<br>
[Flow chart](https://www.canva.com/design/DAFy5DPNmbU/1s5399_q_14I6c-TSU9hxg/edit)
<div markdown="1">

## 4. 구현 기능

<details>
<summary>1. (관리자)수강생 강의 등록</summary>
<div markdown="1">
   프로젝트의 수강생 명단과 수강 정보를 HRD-NET에서 엑셀 파일로 받는다고 가정을 하고 진행하였기에,<br>
   Lo-Pie-Academy에서 진행되는 수강 신청은 관리자만의 기능이 되었습니다.<br>
```TypeScript
//Controller 코드
 @PostMapping("/stuList")
    public String uploadStu(@RequestParam(value = "stud_no[]") List<Integer> stud_no,
                            @RequestParam(value = "occ_NO[]") List<Integer> occ_NO) {
        System.out.println("stud_no : " + stud_no);
        System.out.println("occ_NO : " + occ_NO);
        for (Integer stu : stud_no) {
            for (Integer integer : occ_NO) {
                StudentLecDto studentLecDto = studentLecService.selectClass(stu, integer);
                System.out.println("studentLecDto========================"+studentLecDto);
                if (studentLecDto == null) {
                    studentLecService.insertClass(stu, integer);
                } else {
                    stud_no = null;
                    occ_NO = null;
                    return "null";
                }
            }
        }
        return "redirect:/stuLec/stuList";
    }
```
처음 리스트 형식으로 체크박스의 값을 받지 않았을 때에는 다중 선택을 하면 오류가 발생하여, 체크박스 선택 시 리스트 형식으로 데이터를 받아와 다중 선택 기능 구현하였습니다.
</div>
</details>
<br>
2. (학생)메인 페이지 및 수강 정보
```
@GetMapping("/lecInfo")
    public String getLecInfo(Model model, HttpSession session) {
        AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
        int stud_no = authInfo.getUser_no();
        List<LecDto> listenLecDtos = lecInfoService.listenLecList(stud_no);
        model.addAttribute("listenLecDtos", listenLecDtos);
        return "minho/listenLec/lecInfo";
    }
```
로그인이 성공하게 되면 세션에 저장되는 사용자 번호를 토대로 DB 에서 해당 수강생이 듣는 강의들을 리스트 형식으로 가져오게 하였습니다.


