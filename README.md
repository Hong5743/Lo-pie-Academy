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
<summary>1. (관리자)수강생 강의 등록</summary><br>
 <img src="https://github.com/Hong5743/Lo-pie-Academy/assets/136396772/133327b0-e66d-4c4d-9e06-fef3072954a6" width="600" height="400" alt="메인 페이지 및 수강 정보"/>

프로젝트의 수강생 명단과 수강 정보를 HRD-NET에서 엑셀 파일로 받는다고 가정을 하고 진행하였기에,<br>
Lo-Pie-Academy에서 진행되는 수강 신청은 관리자만의 기능이 되었습니다.<br>

```
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

</details>
<details>
<summary>2. (학생)메인 페이지 및 수강 정보</summary>
<img src="https://github.com/Hong5743/Lo-pie-Academy/assets/136396772/5800f752-38ab-4bb2-976e-5cc31336019e" width="600" height="400" alt="메인 페이지 및 수강 정보"/>
 <br>
 
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
</details>

<details>
 <summary>3. (학생)수강 정보 리스트</summary>
 <img src="https://github.com/Hong5743/Lo-pie-Academy/assets/136396772/abab3cba-2ffe-41fd-acc0-5b47d0730cfa" width="600" height="400" alt="수강 정보 리스트"/>
 
```
 @GetMapping("/lecList")
    public String getLecList(Model model, @RequestParam("occ_NO") int occ_NO,
                             HttpSession session) {
        List<LecListDto> lectList = lectListService.selectLecList(occ_NO);
        model.addAttribute("lectList", lectList);
        AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
        int stud_no = authInfo.getUser_no();
        int countCcimNo = listenLecDao.countCcimNo(occ_NO);
        int countSchsOcs = listenLecDao.countSchsOcs(stud_no, occ_NO);
        Double stud_pg = (double) ((100/countCcimNo) * countSchsOcs);
        lecVideoService.updateStudPg(stud_pg, stud_no, occ_NO);
        LecDto lecDto = lecVideoService.selectOneClass(stud_no, occ_NO);
        if (lecDto.getStud_pg() >= 80) {
            lecVideoService.updateStudSt(stud_no, occ_NO, stud_pg);
        }
        return "minho/listenLec/lecList";
    }
```
         
이전 수강 정보 페이지에서 수강하러 가기를 클릭 시 이 페이지로 이동하게 되며 챕터 개수와 수강 완료한 강의를 select 하고 백분율을 계산하여 진도율 자동 업데이트합니다, 진도율이 80%가 넘어가 수료 가능이라고 DB에 업데이트가 되도록 하여 학생 스스로도 진도율을 확인할 수 있게 구현하였습니다.
</details>
<details>
 <summary>4. 강의 수강</summary>
 <img src="https://github.com/Hong5743/Lo-pie-Academy/assets/136396772/29c15d46-c1f4-4cd0-9b66-fea511e88e48" width="600" height="400" alt="메인 페이지 및 수강 정보"/>

```
// YouTube API 키
 const apiKey = 'AIzaSyArivYMriACjf4a5097KcqUOJLmAuFi0cw';

// YouTube 동영상 ID
const CCIM_videoID = document.getElementById('board_wrap_videoId').getAttribute('videoId');
console.log(CCIM_videoID);

// 동영상 플레이어 변수
let player;

// 마지막으로 기록된 시간
let schs_fnpo = document.querySelector("#board_wrap_fnpo").getAttribute("schsFnpo");

//영상의 총 재생시간 변수
let schs_endpo = document.querySelector("#board_wrap_endpo").getAttribute("schsEnpo");

let ccim_NO = document.querySelector("#board_wrap_ccim_NO").getAttribute("ccimNo");
let occ_NO = document.querySelector("#board_wrap_occ_NO").getAttribute("occNo");

function onYouTubeIframeAPIReady() {
    player = new YT.Player('youtubeVideo', {
        height: '500',
        width: '850',
        videoId: CCIM_videoID,
        events: {
            'onReady': onPlayerReady,
            'onStateChange': onPlayerStateChange,
            'onPlayerPlaybackRateChange': onPlayerPlaybackRateChange
        }
    });
}

//마지막 재생위치에서로 이동해서 플레이
function onPlayerReady(event) {
    event.target.playVideo(); // 플레이어 재생
    player.seekTo(schs_fnpo); // 마지막으로 이동
    RUN_TM = event.target.getDuration(); //재생시간 총 시간에서 5초를 뺌
    schs_endpo = event.target.getDuration(); // 영상의 총 재생 시간을 가져옴
}

// 일정시간간격 반복할 함수(저장용)
let recordInterval;
let finishInterval;

function onPlayerStateChange(event) {
    if (event.data === YT.PlayerState.PLAYING) {
        if (player.getCurrentTime() < schs_fnpo) {
            clearInterval(recordInterval);
        }

        if (event.target.getCurrentTime() > Number(schs_fnpo) + 1) {
            event.target.seekTo(schs_fnpo);
        }

        if (event.target.getCurrentTime() >= RUN_TM) {
            player.pauseVideo();
            player.seekTo(schs_fnpo);
        }
        if (recordInterval) clearInterval(recordInterval);
        if (finishInterval) clearInterval(finishInterval);

        finishPosition();
        finishInterval = setInterval(finishPosition, 1000);

        //5초마다 MAX_POSI와 현재 시간을 저장한다
        if (player.getCurrentTime() > schs_fnpo) {
            recordInterval = setInterval(updatePosition, 5000);
        }
    }

    //일시정지중에는 반복을 멈춘다
    //일시정지한 시간을 기록한다
    if (event.data === YT.PlayerState.PAUSED) {
        clearInterval(recordInterval);
        clearInterval(finishInterval);
        if (recordInterval >= schs_fnpo + 5) {
            if (event.target.getCurrentTime() <= schs_fnpo + 5) {
                updatePosition();
            }
        }
    }
    if (event.data === YT.PlayerState.ENDED) {
        event.target.seekTo(event.target.getDuration() - 1);
        event.target.pauseVideo();
    }

}

// requestPost 함수 정의, 데이터값을 post로 넘기기
function requestPost(schs_fnpo, schs_endpo) {
    schs_fnpo = Math.floor(player.getCurrentTime());
    schs_endpo = Math.floor(player.getDuration());
    ccim_NO = document.querySelector("#board_wrap_ccim_NO").getAttribute("ccimNo");
    occ_NO = document.querySelector("#board_wrap_occ_NO").getAttribute("occNo");
    //해당하는 서버 엔드포인트 URL
    if (schs_fnpo > document.querySelector("#board_wrap_fnpo").getAttribute("schsFnpo")) {
        const url = `/listenLec/savePo?ccim_NO=${ccim_NO}&occ_NO=${occ_NO}&schs_fnpo=${schs_fnpo}&schs_endpo=${schs_endpo}`;
        const data = {
            schs_fnpo: schs_fnpo,
            schs_endpo: schs_endpo
        }
        fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json' // 데이터 형식 지정
            },
            body: JSON.stringify(data) // 객체를 JSON 문자열로 변환하여 전송
        })
            .then(response => // 특정 URL로 리다이렉트
                window.location.href = "/listenLec/lecList?occ_NO=" + occ_NO // 원하는 URL로 바꿔주세요
            ) // 응답을 JSON 형식으로 파싱
            .then(data => console.log('Watch time successfully sent to the server:', data)) // 처리된 데이터를 콘솔에 출력
            .catch(error => console.error('Error:', error)); // 오류 처리
    } else {
        window.location.href = "/listenLec/lecList?occ_NO=" + occ_NO;
    }
}

//시간기록
function updatePosition() {
    schs_fnpo = Math.floor(player.getCurrentTime());
    schs_endpo = schs_endpo > schs_fnpo ? schs_endpo : schs_fnpo; // 두개 변수 비교해서 참일시, 거짓일시 리턴 값
}

//영상 끝나기 x초전에 정지 (마지막 추천영상 안뜨기 위한 함수)
function finishPosition() {
    if (Math.floor(player.getCurrentTime()) >= RUN_TM) {
        player.pauseVideo();
    }
}

//재생속도가 변경될 때 1을 초과하면 1로 변경 (재생속도 빠른배속은 막는 함수)
function onPlayerPlaybackRateChange(event) {
    if (event.target.getPlaybackRate() > 1) {
        event.target.setPlaybackRate(1);
    }
}
```

유튜브 Iframe API의 'onYouTubeIframeAPIReady' 함수를 사용하여 사용하여 유튜브 영상 ID로 유튜브에 등록한 강의를 불러오게 하는 ‘onReady’ 이벤트와 영상 시간 제어를 돕는 ‘onStateChange’ 이벤트 영상의 배속 제어를 위한 ‘onPlayerPlaybackRateChange’ 이벤트, 3가지 이벤트를 설정하였습니다.
<br>
<br>
onStateChange 함수에서는 5초마다 영상의 재생 시간을 기록하는 함수를 설정하였고 영상을 앞으로 돌려도 저장된 시간으로 되돌아가게 설정하였으며 일시정지를 하였을 시 5초마다 반복되는 기록이 멈추게 되며 일시 정지한 시간이 저장됩니다.
<br>
<br>
수강 종료 버튼을 누르게 되면 requestPost 함수를 실행하여 저장할 데이터를 JavaScript를 통해 controller에 전송 후 영상 총 시간과 영상이 마지막으로 저장된 시간이 DB에 데이터가 업데이트 되게 설정하였습니다. 

```
//재생 시간 저장
    @ResponseBody
    @PostMapping(value = "/savePo", produces =  "application/json")
    public String postSaveFnpo(Model model,HttpSession session, @RequestParam("ccim_NO") int ccim_NO,
                             @RequestParam("occ_NO") int occ_NO, @RequestParam(value = "schs_fnpo") int schs_fnpo,
                             @RequestParam(value = "schs_endpo") int schs_endpo) {
        AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
        int stud_no = authInfo.getUser_no();
        LecVideoDto lecVideoDto = lecVideoService.selectLecVideo(ccim_NO, occ_NO);
        model.addAttribute("lecVideo", lecVideoDto);
        model.addAttribute("ccim_NO", ccim_NO);
        model.addAttribute("occ_NO", occ_NO);
        SchsDto schsDto = lecVideoService.selectSchs(stud_no, occ_NO, ccim_NO);
        System.out.println(schsDto);
        model.addAttribute("schsDto", schsDto);
        if (schsDto != null){
            lecVideoService.updatePo(stud_no, occ_NO, ccim_NO, schs_fnpo, schs_endpo);
            if (schsDto.getSchs_fnpo() >= schsDto.getSchs_endpo() - 5){
                int schs_ocs = 1;
                lecVideoService.updateOcs(stud_no, occ_NO, ccim_NO, schs_ocs);
            }
        }
        return "redirect:/listenLec/lecList?occ_NO="+occ_NO;
    }
```

</details>
<details>
 <summary>5. 과제 제출</summary>
 <img src="https://github.com/Hong5743/Lo-pie-Academy/assets/136396772/964b2a30-fcd9-4d1a-9b97-8aea76554618" width="600" height="400" alt="과제 제출"/>

Spring boot의 내장된 MultipartFile을 이용하여 파일을 첨부하면 저장될 디렉토리 설정해 주었으며 파일이 보여질 이름을 UUID로 설정하였습니다.

```
@Service
public class FileServiceMH {
    @Autowired
    FileDaoMH fileDaoMH;
    
    private static final String path = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\minho\\files\\";//저장될 디렉토리 생성및 설정

    public void insertFile(MultipartFile file, int submit_no) throws IOException {
        if (file != null && !file.isEmpty()) {
            // 파일 처리 로직을 구현합니다.
            // 예를 들어, 파일을 저장하거나 데이터베이스에 연동하는 등의 작업을 수행합니다.
            FileDtoMH fileDtoMH = new FileDtoMH();
            UUID uuid = UUID.randomUUID();
            String fileoriginname = file.getOriginalFilename();
            String filename = uuid + "_" + file.getOriginalFilename();
            int filesize = (int) file.getSize();
            File savefile = new File(path, filename);
            file.transferTo(savefile);//세이브 파일 경로에 저장하라는 명령어

            fileDtoMH.setSubmit_no(submit_no);
            fileDtoMH.setFile_uuid(filename);
            fileDtoMH.setFile_path(path + filename);
            fileDtoMH.setFile_nm(fileoriginname);
            fileDtoMH.setFile_cp(filesize);

            fileDaoMH.insertFile(fileDtoMH);
        }
    }
```

Spring boot MultipartFile을 List 형식으로 받아 다중 파일 첨부가 가능하게 구현하였으며, 과제의 파일을 첨부하지 않는 것을 대비해 파일을 첨부하지 않으면 빈 리스트로 초기화하여 파일을 첨부하지 않아도 과제가 제출되는 로직을 컨트롤러에 사용하였으며 드롭존의 사용을 위해 JavaScript를 사용하였습니다.

```
//Controller
@PostMapping("/send")
    public String postSubmit(@RequestParam(name = "files", required = false) List<MultipartFile> files,
                             @RequestParam("stud_no") int stud_no, @RequestParam("occ_no") int occ_no,
                             @RequestParam("amc_no") int amc_no, @RequestParam("submit_ct") String submit_ct
    ) throws IOException {
        try {
            if (files == null) {
                files = Collections.emptyList();//파일이 전송되지 않은 경우 빈 리스트로 초기화
            }
            SubmitDto submitDto = new SubmitDto();
            submitDto.setStud_no(stud_no);
            submitDto.setOcc_NO(occ_no);
            submitDto.setAmc_no(amc_no);
            submitDto.setSubmit_ct(submit_ct);
            submitService.insertSubmit(submitDto);

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    int submit_no = submitService.selectSubmit();
                    fileServiceMH.insertFile(file, submit_no);
                }
            }
            return "redirect:/amc/amcView?amc_no=" + amc_no;
        } catch (NullPointerException e) {
            return "errorPage";
        }
    }
```

```
Dropzone.autoDiscover = false;


$(document).ready(function () {

    // Dropzone 설정
    var dropzone = new Dropzone("#dropzoneForm", {
        url: "/submit/send",
        method: "post",
        autoProcessQueue: false, // 자동으로 보내기. true : 파일 업로드 되자마자 서버로 요청, false : 서버에는 올라가지 않은 상태.
        paramName: "files",  // 파일 파라미터 이름
        uploadMultiple: true,  // 다중 파일 업로드 활성화
        maxFiles: 5,  // 최대 업로드 파일 수
        maxFilesize: 5,  // 최대 파일 크기 (MB)
        parallelUploads: 5,  // 병렬 업로드 수
        dictDefaultMessage: "파일을 여기에 드래그하세요 또는 클릭하세요. 최대 파일 갯수 : 5개",  // 기본 메시지
        dictRemoveFile: "파일 삭제",  // 파일 삭제 버튼 텍스트
        addRemoveLinks: true,  // 파일 추가/삭제 링크 표시 여부
        dictMaxFilesExceeded: "더 이상 파일을 업로드할 수 없습니다.",  // 최대 파일 개수 초과 시 메시지
        init: function () {
            this.on("maxfilesexceeded", function (file) {
                // 최대 파일 업로드 개수 초과 시 동작
                showAlert("최대 5개까지만 업로드 가능합니다.");
                this.removeFile(file); // 초과된 파일 제거
            });
            this.on("complete", function (file) {
                // 업로드가 완료된 후의 동작
                if (this.getUploadingFiles().length === 0 && this.getQueuedFiles().length === 0) {
                    // 모든 파일 업로드가 완료되었을 때, 추가 동작을 수행하거나 폼 제출 등
                    this.removeAllFiles(); // 모든 파일 제거
                }
            });
        }
    });
// Dropzone의 파일 업로드와 관련된 이벤트 핸들러 등록
    dropzone.on("sending", function (file, xhr, formData) {
        // 파일이 업로드되기 전의 동작
        // 추가적인 데이터를 formData에 추가할 수 있음
        if (!formData.has("occ_no")) {
            formData.append("occ_no", document.getElementsByName("occ_no")[0].value);
        }
        if (!formData.has("stud_no")) {
            formData.append("stud_no", document.getElementsByName("stud_no")[0].value);
        }
        if (!formData.has("amc_no")) {
            formData.append("amc_no", document.getElementsByName("amc_no")[0].value);
        }
        if (!formData.has("submit_ct")) {
            formData.append("submit_ct", document.getElementsByName("submit_ct")[0].value);
        }
        formData.append("files", file);

    });
    dropzone.on("success", function (file, response) {
        // 업로드가 완료된 후의 동작
        // 서버에서 전달받은 응답(response)를 확인하여 추가 동작 수행 가능
        console.log("테스트 확인 입니다.");
        // 폼을 서버에 제출
        $("#insert_form").submit();
        window.location.href = '/amc/amcView' + '?amc_no=' + document.getElementsByName("amc_no")[0].value;
    });
// 기타 Dropzone 이벤트 등록 가능
    $("#insert_form").submit(function (event) {
        event.preventDefault();
        event.stopPropagation();
        dropzone.processQueue(); // Dropzone에 파일 업로드 수행
    });
});

function submitAmfi() {
    let dropzone = Dropzone.forElement("#dropzoneForm");
    let form = document.querySelector('form');

    if (dropzone.getQueuedFiles().length === 0) {
        // 파일이 없는 경우
        Swal.fire({
            title: "파일이 없습니다.",
            text: "등록된 파일이 없습니다. 과제만 등록하시겠습니까?",
            icon: "info",
            showCancelButton: true,
            confirmButtonText: "예",
            cancelButtonText: "아니오"
        }).then((result) => {
            if (result.isConfirmed) {
                // 확인을 눌렀을 때의 동작 (예를 들어, 과제 등록)
                console.log("과제 등록");
                if (checkNullAmc()) {
                    form.submit();
                    // 여기에 추가적인 동작을 추가하십시오.
                }
            } else {
                // 아니오를 눌렀을 때의 동작 (예를 들어, 다른 동작 수행)
                console.log("사용자가 아니오를 선택했습니다.");
            }
        });
    } else {
        // 파일이 있는 경우
        Swal.fire({
            title: "파일이 등록되었습니다.",
            text: "파일이 등록되어 있습니다. 과제 등록하시겠습니까?",
            icon: "info",
            showCancelButton: true,
            confirmButtonText: "예",
            cancelButtonText: "아니오"
        }).then((result) => {
            if (result.isConfirmed) {
                // 확인을 눌렀을 때의 동작 (예를 들어, 과제 등록)
                if (checkNullAmc()) {
                    dropzone.processQueue();
                    // 여기에 추가적인 동작을 추가하십시오.
                }
                console.log("과제 등록");
                // 여기에 추가적인 동작을 추가하십시오.
            } else {
                // 아니오를 눌렀을 때의 동작 (예를 들어, 다른 동작 수행)
                console.log("사용자가 아니오를 선택했습니다.");
            }
        });
    }
}

function showAlert(message) {
    Swal.fire({
        icon: 'warning',
        iconColor: '#12192c',
        title: '알림',
        text: message,
        confirmButtonColor: '#3085d6',
        confirmButtonText: '확인'
    });
}
```

</details>

## 5. 아쉬운 점
이번 프로젝트를 통해서 사용하지 않았던 API를 사용하여 좋았던 점도 있었지만, 아직 DB 정규화와 클린 코드가 부족하다는 생각을 많이 느끼게
되었으며 향후 정규화에 대한 분석과 클린 코드에 대한 능력을 향상시키도록 하겠습니다.
