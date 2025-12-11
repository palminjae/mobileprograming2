package com.example.tpt.ui.tip;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tpt.R;

public class TipDetailActivity extends AppCompatActivity {

    LinearLayout container;
    TextView tvTipTitle;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_detail);

        String title = getIntent().getStringExtra("title");

        // 뷰 초기화
        container = findViewById(R.id.container);
        tvTipTitle = findViewById(R.id.tv_tip_title);
        btnBack = findViewById(R.id.btnBack);

        // 제목 설정
        if (title != null) {
            tvTipTitle.setText(title);
        }

        // 뒤로가기 버튼 설정
        btnBack.setOnClickListener(v -> finish());

        // 콘텐츠 추가
        addContent(title);
    }

    private void addText(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(16);
        tv.setTextColor(Color.parseColor("#333333"));
        tv.setBackgroundResource(R.drawable.text_background);
        tv.setPadding(24, 24, 24, 24);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 12, 0, 12);
        tv.setLayoutParams(params);
        container.addView(tv);
    }

    private void addImage(int resId) {
        ImageView img = new ImageView(this);
        img.setImageResource(resId);
        img.setAdjustViewBounds(true);
        img.setPadding(0, 20, 0, 20);
        container.addView(img);
    }

    private void addContent(String title) {
        switch (title) {
            case "기숙사 TIP":
                addText("1. 결핵 검사서나 보건증은 한달 이내에 발급 받은 것이여야 하고 1학기에만 제출하면 된다.");
                addText("2. 입실 동의서는 1학기, 2학기 다 제출해야 한다.");
                addText("3. 세븐일레븐은 김밥, 샌드위치, 도시락이 12시 이후에 물류가 들어온다. 그래서 12시 전에 가면 없는 날이 많기 때문에 12시 이후에 방문하는 것을 추천한다.");
                addText("4. 세븐일레븐, GS25에는 상비약을 팔지 않기 때문에 기숙사 오기 전에 미리 구입해 놓는 것이 좋다.");
                addText("5. 매학기 기숙사 식당에서 혈관 등록을 하고 먹어야 한다.");
                break;

            case "도서관 TIP":
                addImage(R.drawable.img_clicker);
                addText("1. 도서관의 열람실을 이용하려면 클리커 앱을 다운로드 한 후 학번과 비번을 입력해서 로그인을 한다.");
                addText("2. 열람실의 비어있는 좌석을 배정하고 다 이용한 후 나갈 때는 꼭 반납을 해야 한다.");
                addText("3. 책을 대출하고 반납할 때 학생증이 없어도 클리커에 있는 모바일 열람증으로 가능하다.");
                break;

            case "학식 TIP":
                addText("학식을 먹을 때 키오스크 줄이 너무 길다면 잇츠미로 결제가 가능하다.");
                addImage(R.drawable.img_itsme);
                addText("-> 결제를 한 후 본인이 고른 메뉴 줄에 서서 큐알코드를 찍고 먹으면 된다.");
                addText("-> 학식 뿐 아니라 카페도 결제가 가능하다. 카페는 결제를 한 후 상품이 준비되었다고 뜨면 가져가면 된다");
                break;

            case "학생증 TIP":
                addText("1. 신입생");
                addText("- 체크카드");
                addText("-> 개인별 학사정보시스템에서 신청, 학과장 승인후 신청서 출력\n" +
                        "-> 출력한 신청서 및 증명사진을 지참하여 농협방문, 개인별 농협에서 배부한 신청서 작성\n" +
                        "-> 농협에서 학생증 발급 및 배부");
                addText("- 일반학생증");
                addText("-> 개인별 학사정보시스템에서 신청(사진자료가 없는 학생은 미발급)\n" +
                        "-> 학생과에서 학생증 발급\n" +
                        "-> 학생과에서 단대 및 학과를 통해 학생증 배부");
                addText("2. 재학생");
                addText("- 체크학생증");
                addText("-> 개인별 학사정보시스템에서 신청, 학과장 승인후 신청서 출력(본교생 여부 확인을 위해 학과장 승인 절차를 거침,학생과와 관련 없음)\n" +
                        "-> 출력한 신청서 지참하여 농협방문, 개인별 농협에서 배부한 신청서 작성\n" +
                        "-> 농협에서 학생증 발급 및 배부");
                addText("- 일반학생증");
                addText("-> 개인별 학사정보시스템에서 신청, 학과장 승인(학과장 승인 후 그 다음날 신청자료가 학생과로 송부됨)\n" +
                        "-> 학생과에서 학생증 발급\n" +
                        "-> 학생과에서 단대 및 학과를 통해 학생증 배부");
                addImage(R.drawable.img_cwnu_app);
                addText("3. 모바일 학생증\n" +
                        "-> 창원대학교앱에서 즉시 발급 가능하다.");
                break;

            case "수강신청 TIP":
                addText("1. 1학년 1학기는 수강신청 장바구니를 쓰지 못한다.\n" +
                        "그래서 첫 수강신청을 할 때엔 본인이 들을 과목이 대분류의 무엇인지, 소분류의 무엇인지 확인을 하고 외워두는 게 좋다.");
                addText("2. 수강 장바구니는 원하는 건 다 담을 수 있지만 너무 많이 담으면 어디에 있는지 찾는 시간이 오래 걸려서 수강 인원이 다 차기 때문에 너무 많이 담지 않는게 좋다.");
                addText("3. 수강 신청을 컴퓨터에서 할 때 서버가 터졌다면 폰으로 창원대학교 앱에 들어가서 수강 신청을 하면 된다.\n" +
                        " 앱으로 하는 사람 보다 컴퓨터로 하는 사람이 훨씬 많기 때문에 처음부터 앱으로 하는 것도 추천한다.");
                addText("4. 정정기간에 인강이나 교양을 주울 수 있다. 시간이 된다면 틈틈이 새로 고침을 하는 게 좋다.");
                break;
        }
    }
}