<template>
  <div v-show="isInitFullScreen"
    style="width: 100vw;height: 100vh;z-index: 100;background-color: gray;opacity: 0.8;position: absolute;display: flex;align-items: center;justify-content: center"
    @click="handleInitFullScreen">
    <h1 style="font-size: 64px;color: white;">单击任意位置进入全屏</h1>
  </div>
  <a-layout class="layout">
    <a-layout-header>
      <div class="insert" v-if="isWindowWidthGreaterThanHeight()">
        <a-button @click="handleInsert"
          style="width: 70px;height: 40px;font-size: 14px;margin-top: 12px;background-color: #001529;color: gold;border: 1px solid gray;">新增</a-button>
        <a-popconfirm placement="bottom" ok-text="确定" cancel-text="手滑了" @confirm="handleDelete">
          <a-button :disabled="globalData.length == 0" style="width: 70px;margin-left: 30px;height: 40px;font-size: 14px;margin-top: 12px;background-color:
        #001529;color:
        gold;border: 1px solid gray;">删除</a-button>
          <template #title>
            <p>确定删除这道题目吗?</p>
          </template>
        </a-popconfirm>
        <a-button @click="handleUpdate" :disabled="globalData.length == 0"
          style="margin-left: 30px;width: 70px;height: 40px;font-size: 14px;margin-top: 12px;background-color: #001529;color: gold;border: 1px solid gray;">修改</a-button>
        <a-button v-if="isLogin" @click="handleLogout"
          style="margin-left: 30px;width: 70px;height: 40px;font-size: 14px;margin-top: 12px;background-color: #001529;color: gold;border: 1px solid gray;">登出</a-button>
        <a-button v-if="isLogin" @click="handleFullScreen"
          style="margin-left: 30px;width: 70px;height: 40px;font-size: 14px;margin-top: 12px;background-color: #001529;color: gold;border: 1px solid gray;">全屏切换</a-button>
      </div>
      <a-menu v-model:selectedKeys="selectedKeys" theme="dark" mode="horizontal" :style="{ lineHeight: '64px' }">
        <a-menu-item key="1">数据结构</a-menu-item>
        <a-menu-item key="2">计算机组成原理</a-menu-item>
        <a-menu-item key="3">操作系统</a-menu-item>
        <a-menu-item key="4">计算机网络</a-menu-item>
      </a-menu>
    </a-layout-header>
    <a-modal style="margin-top: 5%;" footer="" v-model:open="showExplaination">
      <div style="height: 400px;max-height: 400px;min-height: 400px;overflow: scroll;margin: 5%;">
        <p v-if="explaination != '' && explaination != null" style="text-align: left;font-size: 24px;"
          v-html="explaination"></p>
      </div>
      <div v-if="explaination == '' || explaination == null"
        style="min-height: 400px;display: flex;align-items: center;justify-content: center;">
        <a-empty description="暂无解释">

        </a-empty>
      </div>
    </a-modal>
    <a-modal style="margin-top: 10%;width: 20%;min-width:300px;min-height: 500px" v-model:open="isShowLoginForm"
      footer="" :closable="false" :maskClosable="false">
      <a-form @keyup.enter="handleLogin">
        <a-form-item label="用户名" style="margin-top: 5%;">
          <a-input style="width: 100%;height: 40px;font-size: 24px;padding: 3px;flex-grow: 0 0 50%;"
            v-model:value="loginForm.username"></a-input>
        </a-form-item>
        <a-form-item label="密&nbsp;&nbsp;&nbsp;码" style="margin-top: 5%;">
          <a-input type="password" style="width: 100%;height: 40px;font-size: 24px;padding: 3px;"
            v-model:value="loginForm.password"></a-input>
        </a-form-item>
        <a-form-item v-if="loginForm.username != '' && loginForm.password != ''" label="验证码" style="margin-top: 5%;">
          <a-input type="verifyCode" style="width: 100%;height: 40px;font-size: 24px;padding: 3px;width: 60%;"
            v-model:value="loginForm.verifyCode"></a-input>
          <img id="verifyImg" style="width: 38%;margin-bottom: 5%;margin-left: 2%;" @click="updateVerifyImg" alt="">
        </a-form-item>
        <div style="text-align: center;margin-top: 5%;position: relative">
          <a-button type="primary" style="width: 4rem;font-size: 1rem;height: 8%;padding: 5px;margin-right: 1%;"
            @click="handleRegister" :disabled="registerDisabled">注册</a-button>
          <a-button style="width: 4rem;font-size: 1rem;height: 8%;padding: 5px;margin-left: 1%;" @click="handleLogin"
            :disabled="loginDisabled">登录</a-button>
        </div>
      </a-form>

    </a-modal>
    <!--新增-->
    <a-modal class="editInsert" style="overflow: hidden;" width="60%" v-model:open="showInsert"
      :style="{ marginTop: insertMarginTop + '%', zIndex: 1 }">
      <template #footer>
        <span>
          <a-button key="back" size="large" @click="showInsert = false">取消</a-button>
          <a-button key="submit" type="primary" size="large" @click="submitInsert">提交</a-button>
        </span>
      </template>
      <div class="insertMask" v-if="insertLoading">
        <a-spin :tip="loadingText" :spinning="insertLoading" size="large" />
      </div>
      <a-form style="margin-left: calc(5%)" :model="insertForm" :label-col="labelCol" :wrapper-col="wrapperCol"
        size="large">
        <a-row>
          <a-col :span="12">
            <a-form-item label="科目" style="width: 180%;height: 40px;">
              <a-select v-model:value="insertForm.subject" :options="formSubjects"></a-select>
            </a-form-item>
            <a-form-item label="题目" style="margin-top: 10px;width: 180%;display: flex;">
              <a-textarea @change="handleSearch"
                style="min-height: 100px;max-height: 200px;font-size: 16px;padding: 3%;" :rows="6"
                v-model:value="insertForm.question"
                placeholder="为了最佳的显示效果，可以在适当题目位置进行换行，例如在I,II,III,IV等之前"></a-textarea>
            </a-form-item>
            <a-form-item label="类型" style="margin-top: 10px;width: 180%;">
              <a-select v-model:value="insertForm.optionType" :options="optionTypes" @change="handleOptionTypechange"
                style="width:100%;height: 40px;"></a-select>
            </a-form-item>
            <a-form-item label="答案" style="width: 180%;margin-top: 10px;">
              <a-textarea :rows="4" v-if="!insertForm.optionType" v-model:value="insertForm.answer" placeholder="在此输入答案"
                style="width: 100%;padding-left: 5px"></a-textarea>
              <a-radio-group style="display: flex; width: 100%" v-model:value="insertForm.answer" size="large"
                v-if="insertForm.optionType">
                <a-radio-button style="width: 180%;text-align: center;" value="A">A</a-radio-button>
                <a-radio-button style="width: 180%;text-align: center;" value="B">B</a-radio-button>
                <a-radio-button style="width: 180%;text-align: center;" value="C">C</a-radio-button>
                <a-radio-button style="width: 180%;text-align: center;" value="D">D</a-radio-button>
              </a-radio-group>
            </a-form-item>
            <a-form-item label="选项A" v-if="insertForm.optionType" style="width: 180%;margin-top: 10px;">
              <a-textarea :rows="2" v-model:value="insertForm.optionA" style="padding-left: 5px"></a-textarea>
            </a-form-item>
            <a-form-item label="选项B" v-if="insertForm.optionType" style="width: 180%;margin-top: 10px;">
              <a-textarea :rows="2" v-model:value="insertForm.optionB" style="padding-left: 5px"></a-textarea>
            </a-form-item>
            <a-form-item label="选项C" v-if="insertForm.optionType" style="width: 180%;margin-top: 10px;">
              <a-textarea :rows="2" v-model:value="insertForm.optionC" style="padding-left: 5px"></a-textarea>
            </a-form-item>
            <a-form-item label="选项D" v-if="insertForm.optionType" style="width: 180%;margin-top: 10px;">
              <a-textarea :rows="2" v-model:value="insertForm.optionD" style="padding-left: 5px"></a-textarea>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="备注" style="margin-top: 10px;width: 150%;">
              <a-textarea style="min-height: 100px;max-height: 200px;padding: 3%;" v-model:value="insertForm.explain"
                :rows="6" placeholder="同问题输入框">
              </a-textarea>
            </a-form-item>
            <a-form-item label="导入" style="width: 150%;margin-top: 10px;display: flex;">
              <div style="border: 1px solid gray;width: 100%;border-radius: 8px;padding: 2%;">
                <div style="text-align: center;margin-bottom: 1%;"><a-button style="width:100%;font-size: 1rem"
                    @click="handleOpenWd">从王道课后题导入</a-button></div>
                <a-upload-dragger name="file" :multiple="true" :max-count="1" :custom-request="customRequest"
                  v-model:file-list="fileList" :before-upload="beforeUpload">
                  <p class="ant-upload-drag-icon">
                    <inbox-outlined></inbox-outlined>
                  </p>
                  <p class="ant-upload-text">单击或拖动图片到此以识别</p>
                </a-upload-dragger>
              </div>
            </a-form-item>
          </a-col>
        </a-row>
        <a-drawer :width="500" title="王道题目列表" :open="isWdDrawerOpen" @close="handleCloseWd">
          <div style="height: 100%;position: absolute;">
            <a-back-top :visibilityHeight="100" />
          </div>
          <a-menu :disabled="isWdLoading"
            style="background-color: transparent;width: 100%;display: flex;align-items: center;justify-content: center;user-select: none;-webkit-user-select: none"
            v-model:selectedKeys="wdCurrent" mode="horizontal">
            <a-menu-item key="sjjg">数据结构</a-menu-item>
            <a-menu-item key="jz">计算机组成原理</a-menu-item>
            <a-menu-item key="czxt">操作系统</a-menu-item>
            <a-menu-item key="jsjwl">计算机网络</a-menu-item>
          </a-menu>
          <div style="text-align: center" v-if="!isWdLoading">
            <a-tag class="WTag" color="cyan">注：个别题目和选项可能有误，请检查后再提交</a-tag>
          </div>
          <div v-if="isWdLoading"
            style="position: absolute;width: 100%;height: 30%;margin-left:-24px;display: flex;align-items: center;justify-content: center;">
            <a-spin size="large" :spinning="isWdLoading" tip="加载中..."></a-spin>
          </div>
          <!--以下是王道题目列表实现代码-->
          <div class="wdQuestionList">
            <div v-for="(chapterData, chapter) in chapterGroupedData" :key="chapter">
              <a-tag color="orange" class="WTag">{{ chapterData[0] }}节</a-tag>
              <div>
                <a-tag color="geekblue" class="QTag" v-for="(question, index) in chapterData[1]" :key="question.id"
                  @click="handleWdQuestion(question.id)">{{ question.index
                  }}</a-tag>
              </div>
            </div>
          </div>
          <!--以上是王道题目列表实现代码-->
          <div v-if="!isWdLoading" @click="goBackToTop"
            style="position: absolute;right: 10px;bottom: 10px;width:40px;height: 40px;border-radius: 50%;border: 1px solid gray;display: flex;align-items: center;justify-content: center;cursor: pointer;">
            <VerticalAlignTopOutlined style="font-size: 18px" />
          </div>
        </a-drawer>
      </a-form>
    </a-modal>

    <a-modal style="top: 1%;height: 100%;display: flex;align-items: center;justify-content: center"
      class="cropperContainer" v-model:open="isShowCropper" @cancel="fileList = []" :closable="false"
      :maskClosable="false">
      <div class="cropMask" v-if="cropLoading">
        <a-spin :spinning="cropLoading"></a-spin>
      </div>
      <div class="picContainer">
        <vue-cropper ref="cropperRef" :src="cropperImageUrl" :style="{ width: '100%', maxHeight: '800px' }" :info="true"
          :full="true" :fixed="true" :fixed-number="1" :auto-crop="false" :auto-crop-width="1" :auto-crop-height="1"
          @real-time="onRealTime"></vue-cropper>
      </div>
      <template #footer>
        <a-button size="large" @click="rotateImage('left')">
          <UndoOutlined style="font-size: 24px" />
        </a-button>
        <a-button size="large" @click="rotateImage('right')">
          <RedoOutlined style="font-size: 24px" />
        </a-button>
        <a-button size="large" @click="isShowCropper = false">取消</a-button>
        <a-button size="large" @click="cropAndUpload" type="primary">确认裁剪</a-button>
      </template>
    </a-modal>
    <!--修改-->
    <a-modal class="editInsert" v-model:open="showUpdate" width="36%" style="top: 15%;overflow: hidden;" okText="提交"
      cancelText="取消" @ok="submitUpdate">
      <a-spin :spinning="updateLoading" style="position: absolute;left: 48%;top: 48%;z-index: 5;" size="large" />
      <a-form :model="updateForm" :label-col="labelCol" :wrapper-col="wrapperCol" size="large"
        style="margin-left: 10%;">
        <a-form-item label="科目" style="width: 150%;height: 40px;">
          <a-select disabled v-model:value="updateForm.subject" :options="formSubjects"></a-select>
        </a-form-item>
        <a-form-item label="题目" style="width: 150%;margin-top: 10px;">
          <a-textarea @change="handleSearch" style="min-height: 100px;max-height: 200px;padding: 3%;" :rows="4"
            v-model:value="updateForm.question"></a-textarea>
        </a-form-item>
        <a-form-item label="类型" style="width: 150%;margin-top: 10px;">
          <a-select disabled v-model:value="updateForm.optionType" :options="optionTypes"
            @change="updateForm.answer = ''" style="width: 100%;height: 40px;"></a-select>
        </a-form-item>
        <a-form-item label="答案" style="width: 150%;margin-top: 10px;">
          <a-input v-if="!updateForm.optionType" v-model:value="updateForm.answer"
            style="width: 100%;height: 40px;padding-left: 5px"></a-input>
          <a-radio-group style="display: flex;" v-model:value="updateForm.answer" size="large"
            v-if="updateForm.optionType">
            <a-radio-button style="width: 100%;text-align: center;" value="A">A</a-radio-button>
            <a-radio-button style="width: 100%;text-align: center;" value="B">B</a-radio-button>
            <a-radio-button style="width: 100%;text-align: center;" value="C">C</a-radio-button>
            <a-radio-button style="width: 100%;text-align: center;" value="D">D</a-radio-button>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="选项A" v-if="updateForm.optionType" style="width: 150%;margin-top: 10px;">
          <a-input v-model:value="updateForm.optionA" style="width: 100%;height: 40px;padding-left: 5px"></a-input>
        </a-form-item>
        <a-form-item label="选项B" v-if="updateForm.optionType" style="width: 150%;margin-top: 10px;">
          <a-input v-model:value="updateForm.optionB" style="width: 100%;height: 40px;padding-left: 5px"></a-input>
        </a-form-item>
        <a-form-item label="选项C" v-if="updateForm.optionType" style="width: 150%;margin-top: 10px;">
          <a-input v-model:value="updateForm.optionC" style="width: 100%;height: 40px;padding-left: 5px"></a-input>
        </a-form-item>
        <a-form-item label="选项D" v-if="updateForm.optionType" style="width: 150%;margin-top: 10px;">
          <a-input v-model:value="updateForm.optionD" style="width: 100%;height: 40px;padding-left: 5px"></a-input>
        </a-form-item>
        <a-form-item label="备注" style="width: 150%;margin-top: 10px;">
          <a-textarea style="min-height: 100px;max-height: 200px;padding: 3%;" v-model:value="updateForm.explaination">
          </a-textarea>
        </a-form-item>
      </a-form>
    </a-modal>
    <a-layout-content
      :class="{ 'pcMargin': isWindowWidthGreaterThanHeight1, 'phoneMargin': !isWindowWidthGreaterThanHeight1 }">
      <a-breadcrumb style="margin: 16px 0">
        <a-breadcrumb-item>408</a-breadcrumb-item>
        <a-breadcrumb-item>{{ currentPages[parseInt(selectedKeys) - 1] }}</a-breadcrumb-item>
      </a-breadcrumb>
      <div class="search">
        <a-tag style="padding: 3%;  background-image: linear-gradient(156deg, #95b8dba8, #e9acaf79);cursor: pointer;"
          @click="isDrawerOpen = true">
          <template #icon>
            <BarsOutlined style="font-size: 24px;" />
          </template>
          <span style="font-size: 24px">查看已添加题目列表</span>
        </a-tag>
        <a-drawer :width="400" title="题目列表" :open="isDrawerOpen" @close="isDrawerOpen = false">
          <div style="text-align: center">
            示例：
            <a-tag style="margin: 1%;padding: 1%;" class="tagSelected">当前题目</a-tag>
            <a-tag style="margin: 1%;padding: 1%;" color="green">当天需复习</a-tag>
            <a-tag style="margin: 1%;padding: 1%;" color="orange">1天未复习</a-tag>
            <a-tag style="margin: 1%;padding: 1%;" color="red">优先复习</a-tag>
          </div>
          <div
            style="position: absolute;display: flex;align-items: center;justify-content: center;width: 100%;height: 100%">
            <a-empty v-if="globalData.length == 0" description="暂无题目，快去新增吧"></a-empty>
          </div>
          <div class="questionList">
            <a-tag v-for="(item, index) in globalData" :color="handleColor(index)"
              :class="index != currentId ? 'QTag' : 'QTag tagSelected'" v-if="isDrawerOpen"
              @click="turntoId = index + 1, turnto()">{{ index + 1 }}</a-tag>
          </div>
        </a-drawer>
      </div>
      <div class="contentContainer">
        <div class="contentDiv">
          <div class="next" @click="nextQuestion" v-if="showAnswer && subjects[subsx[selectedKeys - 1]].length != 0">
            <RightOutlined />
          </div>
          <div class="last" @click="lastQuestion" v-if="showAnswer && subjects[subsx[selectedKeys - 1]].length != 0">
            <LeftOutlined />
          </div>
          <div class="explaination" v-if="subjects[subsx[selectedKeys - 1]].length != 0">
            <a-popover @openChange="handleOpenChange" v-model:open="confirmKnowedVisbile" title="请检查答案是否符合预期"
              trigger="click">
              <template #content>
                <p>标记后的题目会在10~20天内安排复习</p>
                <div style="text-align: center;">
                  <a-button style="margin-right: 2%;" type="primary" ghost size="large" @click="confirmKnowed"
                    :disabled="questionLoading">已熟知</a-button>
                  <a-button danger ghost size="large" @click="confirmKnowedVisbile = false"
                    :disabled="questionLoading">记错了</a-button>
                </div>
              </template>
              <a-button :disabled="questionLoading" size="large" style="color: #2dae2d;border-color: #2dae2d;"
                ghost>标记熟知</a-button>
            </a-popover>
            <a-button size="large" :disabled="explaination == '' || explaination == null || questionLoading"
              type="primary" ghost @click="showExplaination = true" style="margin-left: 1%;">查看解释</a-button>
          </div>
          <div class="questionIndex" v-if="subjects[subsx[selectedKeys - 1]].length != 0 && showAnswer">
            <a-tag color="geekblue" style="font-size: 20px;padding: 5px;border-radius: 5px;">{{
    currentId + 1 + '&nbsp;/&nbsp;' + globalData.length }}</a-tag>
          </div>
          <div class="emptyContainer" v-if="isQuestionEmpty">
            <a-empty :description="description"></a-empty>
          </div>
          <div class="spinContainer" v-if="questionLoading">
            <a-spin :spinning="questionLoading" size="large">
            </a-spin>
          </div>
          <a-badge-ribbon v-if="showAnswer && subjects[subsx[selectedKeys - 1]].length != 0" style="font-size: 18px;">
            <div class="question" ref="questionContainer">
              <span class="questionWord" v-for="word in questionWordsList" v-html="word"></span>
            </div>
          </a-badge-ribbon>
          <div class="answer">
            <div class="tiankong" style="display: flex;align-items: center;"
              v-if="showAnswer && !currentOptionType && subjects[subsx[selectedKeys - 1]].length != 0">
              <a-input style="padding-left: 5px;height: 60px;width: 85%;font-size: 24px;" placeholder="在此输入答案"
                :value="inputAnswer" @input="inputAnswer = $event.target.value"
                @keyup.enter="checkAnswerInput"></a-input>
              <a-button style="font-size: 24px;height: 60px;min-width: 15%;padding: 2%;"
                @click="checkAnswerInput">提交</a-button>
            </div>
          </div>
          <div class="xuanze" v-show="showAnswer && currentOptionType && subjects[subsx[selectedKeys - 1]].length != 0">
            <div class="choices" @click="checkAnswer('A')">
              <a-tag :color="selectedColor.A"
                style="margin-right: 5px;font-size: 32px;min-width: 7%;height: 100%;display: flex;align-items: center;justify-content: center;flex-wrap: wrap">A</a-tag>
              <div
                style="display: flex;align-items: center;justify-content: start;flex-wrap:wrap;font-size: 21px;overflow: scroll;width: 100%;">
                <span class="answerWordA" v-for="word in answerWordsList.optionA" v-html="word"></span>
              </div>
            </div>
            <div class="choices" @click="checkAnswer('B')">
              <a-tag :color="selectedColor.B"
                style="margin-right: 5px;font-size: 32px;min-width: 7%;height: 100%;display: flex;align-items: center;justify-content: center;flex-wrap: wrap">B</a-tag>
              <div
                style="display: flex;align-items: center;justify-content: start;flex-wrap:wrap;font-size: 21px;overflow: scroll;width: 100%;">
                <span class="answerWordB" v-for="word in answerWordsList.optionB" v-html="word"></span>
              </div>
            </div>
            <div class="choices" @click="checkAnswer('C')">
              <a-tag :color="selectedColor.C"
                style="margin-right: 5px;font-size: 32px;min-width: 7%;height: 100%;display: flex;align-items: center;justify-content: center;flex-wrap: wrap">C</a-tag>
              <div
                style="display: flex;align-items: center;justify-content: start;flex-wrap:wrap;font-size: 21px;overflow: scroll;width: 100%;">
                <span class="answerWordC" v-for="word in answerWordsList.optionC" v-html="word"></span>
              </div>
            </div>
            <div class="choices" @click="checkAnswer('D')">
              <a-tag :color="selectedColor.D"
                style="margin-right: 5px;font-size: 32px;min-width: 7%;height: 100%;display: flex;align-items: center;justify-content: center;flex-wrap: wrap">D</a-tag>
              <div
                style="display: flex;align-items: center;justify-content: start;flex-wrap:wrap;font-size: 21px;overflow: scroll;width: 100%;">
                <span class="answerWordD" v-for="word in answerWordsList.optionD" v-html="word"></span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </a-layout-content>
    <a-modal v-model:open="isShowToast" style="padding: 3%">
      <h1 style="color: #2485db;">
        欢迎来到408review！
      </h1>
      <h2 style="color: gray;">
        以下是一些常见问题：
      </h2>
      <a-collapse accordion>
        <a-collapse-panel key="1" header="我该如何使用这个平台来复习？">
          <ul>
          <li>每做完一章习题批改并搞懂后，将其中需要多次重复记忆的概念类题目导入，必要时可以在备注中填写自己对于本题的理解，方便自己忘记思路及时想起。</li>
          <li>为了尽量避免“背答案”，本平台每次展示的导入后的题目的顺序和选项顺序都是随机的。</li>
          <li>当导入的题目数达到一定量之后，就需要进行每天复习，建议每天抽空或安排某个时间段来复习。</li>
          <li>由于页面显示效果不同，移动端仅支持做题，建议使用平板或PC导入题目。</li>
          </ul>
        </a-collapse-panel>
        <a-collapse-panel key="2" header="平台是如何安排复习哪些题目的？">
          <ul>
          <li>本平台是根据用户连续答对该题目的次数来安排不同的复习间隔（1、3、5天不等），每日0点刷新当天需复习的题目。</li>
          <li>当连续答对次数大于5次时，该题目复习间隔将固定为5天。</li>
          <li>一旦该题目答错，复习间隔将清空重来。</li>
          <li>当认为该题目已经比较熟练，可以点击右上角标记熟知，本题答案会亮起，比较答案后确认标记，该题目会在10-20天内安排复习。</li>
          </ul>
        </a-collapse-panel>
        <a-collapse-panel key="3" header="如何联系开发者？">
          <p>如果发现问题或有任何意见和建议，欢迎发邮件联系：2636378384@qq.com</p>
        </a-collapse-panel>
      </a-collapse>
      <h2>
        为了方便大家的复习，建议大家牢记新网址：
        <a href="https://cs408.top">https://cs408.top</a>
      </h2>
      <template #footer>
        <a-button @click="handleToastConform" size="large">我知道了</a-button>
      </template>
    </a-modal>
    <a-layout-footer style="text-align: center;background-image: linear-gradient(156deg,#ebf1f7a8,#e1e6f879);">
      408review ©2024 Made by Ardor
    </a-layout-footer>
  </a-layout>
</template>

<script setup>
import { ref, nextTick, onMounted, reactive, watch, computed } from 'vue';
import axios from 'axios'
import { notification } from 'ant-design-vue'
import { RightOutlined, LeftOutlined, InboxOutlined, UndoOutlined, RedoOutlined, BarsOutlined, VerticalAlignTopOutlined } from '@ant-design/icons-vue'
import { md5 } from 'js-md5';
import VueCropper from 'vue-cropperjs';
import 'cropperjs/dist/cropper.css';

const openNotificationWithIcon = (type, text, beizhu, time) => {
  notification[type]({
    message: text,
    description:
      beizhu,
    duration: time
  });
};
const onRealTime = (data) => {

};
const customRequest = () => {

}
const selectedKeys = ref(['1'])
const currentPages = ['数据结构', '计算机组成原理', '操作系统', '计算机网络', '新增', '修改']
const subsx = ['sjjg', 'jz', 'czxt', 'jw']
const cropperImageUrl = ref('');
const cropperRef = ref(null);
let subjects = reactive({
  sjjg: [],
  jz: [],
  czxt: [],
  jw: []
})
let questionLoading = ref(true)
let showAnswer = ref(true)
let showInsert = ref(false)
let insertLoading = ref(false)
let updateLoading = ref(false)
let isWdLoading = ref(false)
let cropLoading = ref(false)
let showExplaination = ref(false)
let isLogin = ref(false)
let isQuestionEmpty = ref(false)
let isDrawerOpen = ref(false)
let isWdDrawerOpen = ref(false)
let isOpenSearchPop = ref(false)
let isInitFullScreen = ref(true)
let showUpdate = ref(false)
let isShowLoginForm = ref(false)
let isShowCropper = ref(false)
let loginDisabled = ref(false)
let registerDisabled = ref(false)
let confirmKnowedVisbile = ref(false)
let isWindowWidthGreaterThanHeight1 = ref(false)
let isShowToast = ref(false)
let chosenChoice = ref('')
let currentId = ref(0)
let wrongTimes = ref(0)
let createTime = ref(0)
let lastIQLength = ref(0)
let insertMarginTop = ref(2)
let question = ref("")
let inputAnswer = ref("")
let currentOptionType = ref(1)
let turntoId = ref("")
let explaination = ref("")
let questionPlaceholder = ref("")
let loadingText = ref("")
let username = ref("")
let lastWdSubject = ref("null")
let wdCurrent = ref(["sjjg"])
let wdItems = ref([{
  key: 'sjjg',
  label: '数据结构',
  title: '数据结构',
  data: []
}, {
  key: 'jz',
  label: '计算机组成原理',
  title: '计算机组成原理',
  data: []
}, {
  key: 'czxt',
  label: '操作系统',
  title: '操作系统',
  data: []
}, {
  key: 'jsjwl',
  label: '计算机网络',
  title: '计算机网络',
  data: []
}])
let wdQuestionData = ref([
])
let globalData = ref([])
let keywords = ref([])
let fileList = ref([])
let questionWordsList = ref([])
let answerWordsList = ref({
  'optionA': [],
  'optionB': [],
  'optionC': [],
  'optionD': []
})
let subjectsCount = ref([])
let description = ref("")
let loginForm = ref({
  'username': '',
  'password': '',
  'verifyCode': ''
})
let currentRotateDeg = ref(0)
let selectedColor = reactive({
  A: "#403d3d42",
  B: "#403d3d42",
  C: "#403d3d42",
  D: "#403d3d42"
})
let lastIds = reactive({
  sjjg: 0,
  jz: 0,
  czxt: 0,
  jw: 0
})
let insertForm = reactive({
  subject: '',
  question: '',
  answer: '',
  optionType: 0,
  optionA: '',
  optionB: '',
  optionC: '',
  optionD: '',
  explain: ''
})
let updateForm = reactive({
  id: -1,
  subject: '',
  question: '',
  answer: '',
  optionType: 1,
  optionA: '',
  optionB: '',
  optionC: '',
  optionD: '',
  explaination: ''
})
let formSubjects = [
  {
    label: '数据结构',
    value: '数据结构'
  },
  {
    label: '计算机组成原理',
    value: '计算机组成原理'
  },
  {
    label: '操作系统',
    value: '操作系统'
  },
  {
    label: '计算机网络',
    value: '计算机网络'
  }
]
let optionTypes = [
  {
    label: '填空题',
    value: 0
  },
  {
    label: '选择题',
    value: 1
  }
]
const labelCol = {
  style: {
    'width': '15%',
    'textAlign': 'center'
  },
};
const wrapperCol = {
  span: 9
};
const rotateImage = (direction) => {
  if (cropperRef.value) {
    if (direction == "left")
      currentRotateDeg.value -= 1
    else if (direction == "right")
      currentRotateDeg.value += 1
    else
      return
    cropperRef.value.rotateTo(currentRotateDeg.value);
  }
};
async function getTargetQuestion(type) {
  let res = await axios({
    'url': '/review408/getTargetQuestion?id=' + globalData.value[currentId.value]['id'],
    'method': 'get',
    'headers': {
      'Authentication': JSON.parse(localStorage.getItem('userInfo'))['data']['token']
    }
  })
  if (type !== "labelRight")
    showUpdate.value = true
  updateForm.question = res.data['data']['question'].replaceAll('<br>', '\n')
  updateForm.subject = res.data['data']['subject']
  updateForm.optionType = res.data['data']['optionType']
  updateForm.optionA = res.data['data']['optionA']
  updateForm.optionB = res.data['data']['optionB']
  updateForm.optionC = res.data['data']['optionC']
  updateForm.optionD = res.data['data']['optionD']
  updateForm.explaination = res.data['data']['explaination']
  updateForm.answer = res.data['data']['answer']
  updateForm.id = res.data['data']['id']
  setTimeout(() => {
    if (type !== "labelRight")
      updateLoading.value = false
  }, 300);
}
function handleUpdate() {
  updateLoading.value = true
  if (globalData.value.length != 0) {
    getTargetQuestion()
    showUpdate.value = true
  }
  else
    openNotificationWithIcon('error', '暂无可以修改的题目', '', 2)
}
function handleOptionTypechange() {
  insertForm.answer = ''
  // insertMarginTop.value = -2
  if (insertForm.optionType == 0 && insertMarginTop.value < 5)
    insertMarginTop.value += 2
  else if (insertForm.optionType == 1 && insertMarginTop.value > 0)
    insertMarginTop.value -= 2
  document.querySelector('.editInsert').style.transition = "0.5s ease-in-out"
}
function handleResetMarginTop() {
  insertForm.optionType = 0
  insertMarginTop.value = -2
}
function handleLogin() {
  loginDisabled.value = true
  if (loginForm.value.username != '' && loginForm.value.password != '' && loginForm.value.username != null && loginForm.value.password != null) {
    let password = md5.hex(loginForm.value.password)
    async function login() {
      let res = await axios({
        method: 'post',
        url: '/login',
        data:
        {
          'username': loginForm.value.username,
          'password': password,
          'verifyCode': loginForm.value.verifyCode
        }
      })
      if (res.data['message'] == "登录成功") {
        openNotificationWithIcon('success', '登录成功', '', 2)
        setTimeout(() => {
          checkLogin()
        }, 100);
        isLogin.value = true
        isShowLoginForm.value = false
        localStorage.setItem('userInfo', JSON.stringify(res.data))
        getQuestions()
        setTimeout(() => {
          loginDisabled.value = false
        }, 2000);
      }
      else {
        openNotificationWithIcon('error', res.data['message'], '', 2)
        updateVerifyImg()
        setTimeout(() => {
          loginDisabled.value = false
        }, 2000);
      }
    }
    login()
  }
  else {
    openNotificationWithIcon('error', '用户名或密码不能为空', '', 2)
    setTimeout(() => {
      loginDisabled.value = false
    }, 1000);
  }
}
function handleRegister() {
  registerDisabled.value = true
  if (loginForm.value.username != '' && loginForm.value.password != '' && loginForm.value.username != null && loginForm.value.password != null) {
    let password = md5.hex(loginForm.value.password)
    async function register() {
      let res = await axios({
        method: 'post',
        url: '/checkRegister',
        data: {
          'username': loginForm.value.username,
          'password': password,
          'verifyCode': loginForm.value.verifyCode
        }
      })
      if (res.data['message'] == '注册成功!') {
        openNotificationWithIcon('success', '注册成功!', '', 1)
        registerDisabled.value = false
        updateVerifyImg()
      }
      else {
        openNotificationWithIcon('error', res.data['message'], '', 1)
        updateVerifyImg()
        registerDisabled.value = false
      }
    }
    register()
  }
  else {
    openNotificationWithIcon('error', '用户名或密码不能为空', '', 2)
    setTimeout(() => {
      registerDisabled.value = false
    }, 1000);
  }
}
function handleLogout() {
  questionLoading.value = true
  localStorage.removeItem('userInfo')
  setTimeout(() => {
    openNotificationWithIcon('success', '成功退出登录', '', 1)
    questionLoading.value = false
    setTimeout(() => {
      window.location.reload()
    }, 1000);
  }, 500);
}
function checkLogin() {
  if ((window.localStorage.getItem('userInfo'))) {
    async function toCheckToken() {
      try {
        var res = await axios({
          method: 'get',
          url: '/review408/initData',
          headers: {
            'Authentication': JSON.parse(localStorage.getItem('userInfo'))['data']['token']
          }
        })

        switch (res.data['message']) {
          case "登录状态正常":
            isLogin.value = true
            username.value = JSON.parse(localStorage.getItem('userInfo'))['data']['username']
            getQuestions()
            subjectsCount.value = res.data['data']
            if (subjectsCount.value[subsx[selectedKeys.value - 1]] == 0)
              description.value = "暂无题目，快来添加吧！"
            else
              description.value = "恭喜！今日该科目的错题全部复习完成！"
            break;
          case "登录过期":
            openNotificationWithIcon('error', '登录过期，请重新登录', '', 1)
            isShowLoginForm.value = true

            break;
          default:
            openNotificationWithIcon('error', '请先登录', '', 1)
            isShowLoginForm.value = true
        }
      }
      catch {
        openNotificationWithIcon('error', '网络异常', '', 2)
        setTimeout(() => {
          window.location.reload()
        }, 3000);
      }
    }
    toCheckToken()
  }
  else {
    openNotificationWithIcon('error', '请先登录', '', 1)
    isShowLoginForm.value = true
  }
}
function convertTimestamp(timestamp) {
  const milliseconds = timestamp * 1000;
  const date = new Date(milliseconds);
  const year = date.getFullYear();
  const month = date.getMonth() + 1; // 月份是从0开始计数的，因此需要加1  
  const day = date.getDate();
  let hours = date.getHours();
  let minutes = date.getMinutes();
  let seconds = date.getSeconds();
  if (hours < 10)
    hours = '0' + hours
  if (minutes < 10)
    minutes = '0' + minutes
  if (seconds < 10)
    seconds = '0' + seconds
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

function submitInsert() {
  insertLoading.value = true
  insertForm.question = insertForm.question.replaceAll('\n', '<br>')
  insertForm.explain = insertForm.explain.replaceAll('\n', '<br>')
  try {
    async function insert() {
      let res = await axios({
        url: '/review408/insertReview',
        method: 'post',
        data: JSON.parse(JSON.stringify(insertForm)),
        headers: {
          'Authentication': JSON.parse(localStorage.getItem('userInfo'))['data']['token']
        }
      })
      if (res.data['message'] == "insert successfully!") {
        setTimeout(() => {
          openNotificationWithIcon('success', '新增成功', '', 2)
          insertLoading.value = false
          insertForm.question = ""
          insertForm.answer = ""
          // insertForm.optionType = 1
          insertForm.optionA = ""
          insertForm.optionB = ""
          insertForm.optionC = ""
          insertForm.optionD = ""
          insertForm.explain = ""
        }, 500);
        getQuestions()
      }
      else {
        setTimeout(() => {
          openNotificationWithIcon('error', '系统异常', '', 2)
          insertLoading.value = false
        }, 500);

      }
    }
    if (insertForm.subject.length < 4 || insertForm.answer == '' || insertForm.answer == null || insertForm.subject == null) {
      openNotificationWithIcon('error', '参数不完整（可能是答案未填写）', '', 2)
      insertLoading.value = false
    }
    else {
      insert()
    }
  }
  catch (e) {
    openNotificationWithIcon('error', '系统异常', '', 2)
    insertLoading.value = false
  }
}
let ro = null
function handleOpenChange(visible) {
  if (visible)
    ro = handleBeforeLabel()
  else {
    if (globalData.value[currentId.value]['optionType'] == 0) {
      inputAnswer.value = ''
      return
    }
    ro = ro.then(ro => {
      return ro
    }).then(ro => {
      selectedColor[ro] = "#403d3d42"
      ro = null
    })
  }
}
function handleBeforeLabel() {

  async function labelRight() {
    await getTargetQuestion("labelRight")
    if (globalData.value[currentId.value]['optionType'] == 0) {
      inputAnswer.value = updateForm.answer
      return;
    }
    let rightAnswer = updateForm['option' + updateForm.answer]
    let rightOption = ''
    let options = ['A', 'B', 'C', 'D']
    for (let i = 0; i < options.length; i++) {
      if (rightAnswer == globalData.value[currentId.value]['option' + options[i]]) {
        rightOption = options[i]
        break
      }
    }
    selectedColor[rightOption] = "rgba(104, 191, 150, 0.67)"
    return rightOption
  }
  return labelRight()

}
function confirmKnowed() {
  questionLoading.value = true
  async function labelKnowed() {
    let res = await axios({
      url: '/review408/labelKnowed?id=' + globalData.value[currentId.value]['id'],
      method: 'get',
      headers: {
        'Authentication': JSON.parse(localStorage.getItem('userInfo'))['data']['token']
      }
    })
    if (res.data['message'] == "ok") {
      handleOpenChange()
      openNotificationWithIcon('success', '标记熟知成功', '', 1)
      getQuestions()
      currentId.value = 0
      confirmKnowedVisbile.value = false
    }
    else {
      openNotificationWithIcon('error', '网络异常', '', 1)
      questionLoading.value = false
    }

  }
  setTimeout(() => {
    labelKnowed()
  }, 800);
}
function updateVerifyImg() {
  setTimeout(() => {
    let vi = document.getElementById("verifyImg")
    vi.src = "/review408/getVerifyImg?u=" + loginForm.value.username + '&t=' + new Date().getTime()
    loginForm.value.verifyCode = ''
  }, 110);
}
function submitUpdate() {
  updateLoading.value = true
  updateForm.question = updateForm.question.replaceAll('\n', '<br>')
  updateForm.explaination = updateForm.explaination.replaceAll('\n', '<br>')
  async function update() {
    let res = await axios({
      url: '/review408/updateReview',
      method: 'post',
      data: JSON.parse(JSON.stringify(updateForm)),
      headers: {
        'Authentication': JSON.parse(localStorage.getItem('userInfo'))['data']['token']
      }
    })
    if (res.data['message'] == "update successfully!") {
      setTimeout(() => {
        openNotificationWithIcon('success', '修改成功', '', 2)
        updateLoading.value = false
        showUpdate.value = false
        currentId.value = 0
        getQuestions()
        setTimeout(() => {
          getExplaination()
        }, 300);
      }, 500);

    }
    else {
      setTimeout(() => {
        openNotificationWithIcon('error', '系统异常', '', 2)
        insertLoading.value = false
      }, 500);

    }
  }
  update()
}
const beforeUpload = file => {

  const reader = new FileReader();
  reader.readAsDataURL(file);
  reader.onload = (e) => {
    cropperImageUrl.value = e.target.result;
    // 当图片加载完成后，初始化 cropper
    if (!document.fullscreenElement)//防止移动端请求文件时退出全屏
      handleFullScreen()
    isShowCropper.value = true
    nextTick(() => {
      if (cropperRef.value) {
        cropperRef.value.replace(cropperImageUrl.value); // 假设 vue-cropperjs 提供了一个 replace 方法来设置 src  
        fileList = []
      }
    });
  };
  return false; // 阻止antd的Upload组件自动上传  
};

const cropAndUpload = () => {
  cropLoading.value = true
  async function upload(data) {
    let res = await axios({
      url: 'https://yourHost/ocr',
      method: 'post',
      data: {
        "imageData": data
      }
    })

    currentRotateDeg.value = 0
    let receivedData = res.data['data']
    if (res.data['message'] == "ok") {
      fileList.value = []
      insertForm.question = receivedData['question']
      insertForm.optionType = receivedData['optionType']
      insertForm.optionA = receivedData['optionA']
      insertForm.optionB = receivedData['optionB']
      insertForm.optionC = receivedData['optionC']
      insertForm.optionD = receivedData['optionD']
      if (receivedData['probability'] > 1.8)
        insertForm.subject = receivedData['subject']
      insertLoading.value = false
      openNotificationWithIcon('success', '识别成功', '', 2)
      handleOptionTypechange()
      document.querySelector('.editInsert').style.transition = "0.5s ease-in-out"
    }
    else {
      openNotificationWithIcon('error', '识别题目类型失败，请手动调整', '', 2)
      insertForm.question = receivedData['wordList']
      insertForm.optionType = 0
      insertForm.optionA = ''
      insertForm.optionB = ''
      insertForm.optionC = ''
      insertForm.optionD = ''
      if (receivedData['probability'] > 1.8)
        insertForm.subject = receivedData['subject']
      insertLoading.value = false
      handleOptionTypechange()
      document.querySelector('.editInsert').style.transition = "0.5s ease-in-out"
    }
    setTimeout(() => {
      cropLoading.value = false
      isShowCropper.value = false
    }, 300);
  }
  if (cropperRef.value && cropperRef.value.getCroppedCanvas) {
    const canvas = cropperRef.value.getCroppedCanvas();
    if (canvas) {
      const base64 = canvas.toDataURL('image/jpeg');
      upload(base64)
      setTimeout(() => {
        if (cropLoading.value) {
          openNotificationWithIcon('error', '系统异常，请联系管理员', '', 2)
          cropLoading.value = false
        }
      }, 3000);
    } else {
      console.error('Canvas is null. Make sure the cropper is ready and an image is loaded.');
    }
  } else {
    console.error('Cropper reference is null or getCroppedCanvas is not a function.');
  }
};
function splitStringExceptBr(str) {
  const result = [];
  let inBr = false;
  let i = 0
  for (let i = 0; i < str.length; i++) {
    const char = str[i];
    if (char === '<' && str[i + 1] == 'b' && str[i + 2] == 'r') {
      inBr = true;
    } else if (char === '>' && inBr) {
      inBr = false;
      result.push('<br>');
    } else if (!inBr)
      result.push(char)
  }
  return result;
}
function clearShowClass() {
  if (document.querySelectorAll('.questionWord') == null)
    return
  if (document.querySelectorAll('.answerWordA') == null)
    return

  for (let i = 0; i < questionWordsList.value.length; i++)
    document.querySelectorAll('.questionWord')[i].classList.remove('showQuestion')
  for (let i = 0; i < answerWordsList.value.optionA.length; i++)
    document.querySelectorAll('.answerWordA')[i].classList.remove('showAnswer')
  for (let i = 0; i < answerWordsList.value.optionB.length; i++)
    document.querySelectorAll('.answerWordB')[i].classList.remove('showAnswer')
  for (let i = 0; i < answerWordsList.value.optionC.length; i++)
    document.querySelectorAll('.answerWordC')[i].classList.remove('showAnswer')
  for (let i = 0; i < answerWordsList.value.optionD.length; i++)
    document.querySelectorAll('.answerWordD')[i].classList.remove('showAnswer')
  questionWordsList.value = []
  answerWordsList.value.optionA = []
  answerWordsList.value.optionB = []
  answerWordsList.value.optionC = []
  answerWordsList.value.optionD = []
}
function addShowClass() {
  try {
    if (document.querySelectorAll('.questionWord') == null)
      return
    let timers = []
    for (let i = 0; i < questionWordsList.value.length; i++) {
      timers.push(setTimeout(() => {
        document.querySelectorAll('.questionWord')[i].classList.add('showQuestion')
      }, 10 * (i + 1)))
    }
    for (let i = 0; i < answerWordsList.value.optionA.length; i++) {
      timers.push(setTimeout(() => {
        document.querySelectorAll('.answerWordA')[i].classList.add('showAnswer')
      }, 15 * (i + 1)))
    } for (let i = 0; i < answerWordsList.value.optionB.length; i++) {
      timers.push(setTimeout(() => {
        document.querySelectorAll('.answerWordB')[i].classList.add('showAnswer')
      }, 20 * (i + 1)))
    }
    for (let i = 0; i < answerWordsList.value.optionC.length; i++) {
      timers.push(setTimeout(() => {
        document.querySelectorAll('.answerWordC')[i].classList.add('showAnswer')
      }, 25 * (i + 1)))
    }
    for (let i = 0; i < answerWordsList.value.optionD.length; i++) {
      timers.push(setTimeout(() => {
        document.querySelectorAll('.answerWordD')[i].classList.add('showAnswer')
      }, 30 * (i + 1)))
    }
  }
  catch (e) {

  }
}
function getQuestions(type) {
  clearShowClass()
  try {
    async function get() {
      let res = await axios({
        'url': '/review408/getQuestions?subject=' + currentPages[parseInt(selectedKeys.value - 1)],
        'method': 'get',
        'headers': {
          'Authentication': JSON.parse(localStorage.getItem('userInfo'))['data']['token']
        }
      })
      if (currentPages[parseInt(selectedKeys.value - 1)] == '数据结构')
        subjects.sjjg = res.data['data']
      else if (currentPages[parseInt(selectedKeys.value - 1)] == '计算机组成原理')
        subjects.jz = res.data['data']
      else if (currentPages[parseInt(selectedKeys.value - 1)] == '操作系统')
        subjects.czxt = res.data['data']
      else if (currentPages[parseInt(selectedKeys.value - 1)] == '计算机网络')
        subjects.jw = res.data['data']
      if (res.data['data'].length != 0) {
        globalData.value = res.data['data'].sort((a, b) => {
          return a.nextReviewTime - b.nextReviewTime;
        });
        if (globalData.value.length != 1) {
          setTimeout(() => {
            question.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['question']
            questionWordsList.value = splitStringExceptBr(question.value)
            currentOptionType.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionType']
            if (currentOptionType.value == 1) {
              answerWordsList.value.optionA = splitStringExceptBr(JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionA'])
              answerWordsList.value.optionB = splitStringExceptBr(JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionB'])
              answerWordsList.value.optionC = splitStringExceptBr(JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionC'])
              answerWordsList.value.optionD = splitStringExceptBr(JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionD'])
            }
            wrongTimes.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['wrongTimes']
            createTime.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['createTime']
            showAnswer.value = true
            isQuestionEmpty.value = false
            questionLoading.value = false
            getExplaination()
            addShowClass()
          }, 300);
        }
        else {
          setTimeout(() => {
            question.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][0]['question']
            questionWordsList.value = splitStringExceptBr(question.value)
            if (currentOptionType.value == 1) {
              answerWordsList.value.optionA = splitStringExceptBr(JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionA'])
              answerWordsList.value.optionB = splitStringExceptBr(JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionB'])
              answerWordsList.value.optionC = splitStringExceptBr(JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionC'])
              answerWordsList.value.optionD = splitStringExceptBr(JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionD'])
            }
            currentOptionType.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][0]['optionType']
            wrongTimes.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][0]['wrongTimes']
            createTime.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][0]['createTime']
            showAnswer.value = true
            questionLoading.value = false
            isQuestionEmpty.value = false
            addShowClass()
            getExplaination()
          }, 300);
        }
      }
      else {
        globalData.value = []
        isQuestionEmpty.value = true
        questionLoading.value = false
        getExplaination()
      }
    }
    get()
  }
  catch {
    openNotificationWithIcon('error', '网络异常', '', 2)
    window.location.reload()
  }
}
function getExplaination() {
  if (JSON.parse(JSON.stringify(subjects))[subsx[(selectedKeys.value - 1)]][currentId.value] != undefined)
    explaination.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['explaination']
}
function lastQuestion() {
  questionLoading.value = true
  setTimeout(() => {
    if (currentId.value != 0)
      clearShowClass()
  }, 450);
  setTimeout(() => {
    if (currentId.value != 0) {
      currentId.value -= 1
      question.value = ""
      lastIds[subsx[selectedKeys.value - 1]] = currentId.value
      question.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['question']
      questionWordsList.value = splitStringExceptBr(question.value)
      answerWordsList.value.optionA = splitStringExceptBr(JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionA'])
      answerWordsList.value.optionB = splitStringExceptBr(JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionB'])
      answerWordsList.value.optionC = splitStringExceptBr(JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionC'])
      answerWordsList.value.optionD = splitStringExceptBr(JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionD'])
      currentOptionType.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionType']
      wrongTimes.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['wrongTimes']
      createTime.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['createTime']
      localStorage.setItem('lastIds', JSON.stringify(lastIds))
      questionLoading.value = false
      getExplaination()
      addShowClass()
    }
    else {
      openNotificationWithIcon('error', '没有上一条了', '', '2')
      questionLoading.value = false
    }
  }, 500);
}
function nextQuestion() {
  questionLoading.value = true
  setTimeout(() => {
    if (currentId.value != JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]].length - 1)
      clearShowClass()
  }, 450);
  setTimeout(() => {
    if (currentId.value != JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]].length - 1) {
      question.value = ""
      currentId.value += 1
      lastIds[subsx[selectedKeys.value - 1]] = currentId.value
      question.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['question']
      questionWordsList.value = splitStringExceptBr(question.value)
      answerWordsList.value.optionA = splitStringExceptBr(JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionA'])
      answerWordsList.value.optionB = splitStringExceptBr(JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionB'])
      answerWordsList.value.optionC = splitStringExceptBr(JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionC'])
      answerWordsList.value.optionD = splitStringExceptBr(JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionD'])
      currentOptionType.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionType']
      wrongTimes.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['wrongTimes']
      createTime.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['createTime']
      localStorage.setItem('lastIds', JSON.stringify(lastIds))
      questionLoading.value = false
      getExplaination()
      addShowClass()
    }
    else {
      openNotificationWithIcon('error', '没有下一条了', '', '2')
      questionLoading.value = false
    }
  }, 500);
}
function turnto() {
  if (!questionLoading.value && parseInt(turntoId.value - parseInt(1)) != currentId.value && !isNaN(parseFloat(turntoId.value)) && isFinite(turntoId.value)) {
    setTimeout(() => {
      clearShowClass()
    }, 450);
    questionLoading.value = true
    setTimeout(() => {
      currentId.value = parseInt(turntoId.value - parseInt(1))
      lastIds[subsx[selectedKeys.value - 1]] = currentId.value
      question.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['question']
      questionWordsList.value = splitStringExceptBr(question.value)
      currentOptionType.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionType']
      if (currentOptionType.value == 1) {
        answerWordsList.value.optionA = splitStringExceptBr(JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionA'])
        answerWordsList.value.optionB = splitStringExceptBr(JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionB'])
        answerWordsList.value.optionC = splitStringExceptBr(JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionC'])
        answerWordsList.value.optionD = splitStringExceptBr(JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['optionD'])
      }
      wrongTimes.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['wrongTimes']
      createTime.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['createTime']
      turntoId.value = ""
      localStorage.setItem('lastIds', JSON.stringify(lastIds))
      questionLoading.value = false
      addShowClass()
    }, 500);
  }
  else
    openNotificationWithIcon('error', '点击过快', '', '1')
}


function handleSearch() {
  if (insertForm.subject.length < 4 && insertForm.question.length != 0) {
    openNotificationWithIcon('error', '请先选择科目', '', 1)
    return
  }
  if (insertForm.question.length < 1) {
    isOpenSearchPop.value = false
    questionPlaceholder.value = ''
  }
}
function handleInsert() {
  loadingText.value = "加载中..."
  showInsert.value = true
  insertLoading.value = true
  insertMarginTop.value = 1
  insertForm.subject = ''
  insertForm.question = ''
  insertForm.answer = ''
  insertForm.optionA = ''
  insertForm.optionB = ''
  insertForm.optionC = ''
  insertForm.optionD = ''
  insertForm.optionType = 0
  setTimeout(() => {
    insertLoading.value = false
    fileList = []
  }, 300);
}
function handleDelete() {
  async function del() {
    let res = await axios({
      url: "/review408/deleteReview?id=" + globalData.value[currentId.value]['id'],
      method: 'get',
      headers: {
        'Authentication': JSON.parse(localStorage.getItem('userInfo'))['data']['token']
      }
    })
    if (res.data['message'] == "delete successfully!") {
      getQuestions()
      openNotificationWithIcon('success', '删除成功', '', 1)
    }
    else {
      openNotificationWithIcon('error', '删除失败，请联系管理员', '', 1)
    }
  }
  del()
}
function checkAnswerInput() {
  questionLoading.value = true
  setTimeout(() => {
    async function check() {
      let res = await axios({
        url: "/review408/checkAnswer",
        method: 'post',
        data: {
          'id': globalData.value[currentId.value]['id'],
          'answer': inputAnswer.value
        },
        headers: {
          'Authentication': JSON.parse(localStorage.getItem('userInfo'))['data']['token']
        }
      })
      if (res.data['message'] == "right") {
        openNotificationWithIcon('success', '回答正确', '', 1)
        questionLoading.value = false
        inputAnswer.value = ""
        setTimeout(() => {
          if (currentId.value == JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]].length - 1) {
            questionLoading.value = false
            inputAnswer.value = ""
            setTimeout(() => {
              currentId.value = 0
              getQuestions()
              questionLoading.value = true
              if (globalData.value.length != 0) {
                setTimeout(() => {
                  //question.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['question']
                  questionLoading.value = false
                  //getExplaination()
                }, 1000);
              }
              else {
                questionLoading.value = false
              }
            }, 1000);
          }
          else {
            nextQuestion()
          }
        }, 1000);
      }
      else if (res.data['message'] == "wrong") {
        openNotificationWithIcon('error', '回答错误', '', 2)
        questionLoading.value = false
      }
      else {
        openNotificationWithIcon('error', '网络异常', '', 2)
        questionLoading.value = false
      }
    }
    if (questionLoading.value)
      check()
  }, 500);
}
function checkAnswer(choice) {
  questionLoading.value = true
  chosenChoice.value = choice
  let chosenString = ""
  if (choice == 'A')
    chosenString = subjects[subsx[selectedKeys.value - 1]][currentId.value]['optionA']
  else if (choice == 'B')
    chosenString = subjects[subsx[selectedKeys.value - 1]][currentId.value]['optionB']
  else if (choice == 'C')
    chosenString = subjects[subsx[selectedKeys.value - 1]][currentId.value]['optionC']
  else
    chosenString = subjects[subsx[selectedKeys.value - 1]][currentId.value]['optionD']
  async function check() {
    let res = await axios({
      url: "/review408/checkAnswer",
      method: 'post',
      data: {
        'id': globalData.value[currentId.value]['id'],
        'answer': chosenString
      },
      headers: {
        'Authentication': JSON.parse(localStorage.getItem('userInfo'))['data']['token']
      }
    })
    if (res.data['message'] == "right") {
      selectedColor[chosenChoice.value] = "rgba(104, 191, 150, 0.67)"
      setTimeout(() => {
        document.querySelector('.xuanze').style.pointerEvents = "none"
      }, 300);
      openNotificationWithIcon('success', '回答正确', '', 1)
      setTimeout(() => {
        setTimeout(() => {
          if (globalData.value.length != 0)
            document.querySelector('.xuanze').style.pointerEvents = ""
        }, 300);
        selectedColor[chosenChoice.value] = "#403d3d42"
        if (currentId.value == JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]].length - 1) {
          //questionLoading.value = false
          inputAnswer.value = ""
          setTimeout(() => {
            questionLoading.value = true
            showAnswer.value = false
            currentId.value = 0
            getQuestions()
            setTimeout(() => {
              question.value = ""
              lastIds[subsx[selectedKeys.value - 1]] = currentId.value
              if (globalData.value.length != 0) {
                question.value = JSON.parse(JSON.stringify(subjects))[subsx[selectedKeys.value - 1]][currentId.value]['question']
                showAnswer.value = true
                //questionLoading.value = false
                getExplaination()
              }
              else {
                questionLoading.value = false
              }
            }, 1000);
          }, 1000);
        }
        else {
          nextQuestion()
        }
      }, 1000);
    }
    else if (res.data['message'] == 'wrong') {
      selectedColor[chosenChoice.value] = "red"
      let selectedIndex = -1
      switch (chosenChoice.value) {
        case 'A':
          selectedIndex = 0
          break;
        case 'B':
          selectedIndex = 1
          break
        case 'C':
          selectedIndex = 2
          break
        case 'D':
          selectedIndex = 3
          break
        default:
          break;
      }
      openNotificationWithIcon('error', '回答错误', '', 2)
      questionLoading.value = false
      setTimeout(() => {
        selectedColor.A = "#403d3d42"
        selectedColor.B = "#403d3d42"
        selectedColor.C = "#403d3d42"
        selectedColor.D = "#403d3d42"
      }, 1000);
    }
    else {
      openNotificationWithIcon('error', '网络异常', '', 2)
    }
  }
  check()
}
function handleColor(index) {
  if (globalData.value[index].nextReviewTime == null)
    return "green"
  let time = (Math.floor(new Date().getTime() / 1000) - globalData.value[index].nextReviewTime)
  let days = Math.floor(time / 86400)
  switch (days) {
    case 0:
      return "green"
    case 1:
      return "orange"
    default:
      return "red"
  }
}
function handleInitFullScreen() {
  handleFullScreen()
  isInitFullScreen.value = false
}
function handleFullScreen() {
  if (document.fullscreenElement) {
    document.exitFullscreen()
  }
  else {
    document.documentElement.requestFullscreen()
  }
}
function getWdQuestions(subject) {
  isWdLoading.value = true
  wdQuestionData.value = []
  let isCached = false
  if (subject == null) {
    subject = "数据结构"
  }
  lastWdSubject.value = subject
  for (let i = 0; i < wdItems.value.length; i++) {
    if (wdItems.value[i].label == subject && wdItems.value[i].data.length != 0) {
      isWdLoading.value = false
      isCached = true
      nextTick(() => {
        wdQuestionData.value = JSON.parse(JSON.stringify(wdItems.value[i].data))
      })
      break
    }
  }
  if (!isCached)
    get()
  async function get() {
    let res = await axios({
      url: "/review408/getWdQuestions?subject=" + subject,
      method: 'get',
      headers: {
        'Authentication': JSON.parse(localStorage.getItem('userInfo'))['data']['token']
      }
    })
    if (res.data['message'] == "ok") {
      setTimeout(() => {
        isWdLoading.value = false
        wdQuestionData.value = res.data['data']
        for (let i = 0; i < wdItems.value.length; i++) {
          if (wdItems.value[i].label == subject)
            wdItems.value[i].data = wdQuestionData.value
        }
      }, 100);
    }
    else {
      openNotificationWithIcon('error', '网络异常', '', 2)
    }

  }

}
function handleOpenWd() {

  isWdDrawerOpen.value = true
  if (lastWdSubject.value != "null")
    wdCurrent.value = [lastWdSubject.value]
  else
    wdCurrent.value = ['sjjg']
  // getWdQuestions()

}
function handleCloseWd() {
  isWdDrawerOpen.value = false
  document.querySelectorAll('.ant-menu-item').forEach(item => {
    item.classList.remove('ant-menu-item-active')
  })
}
function goBackToTop() {
  document.querySelector('.wdQuestionList').scrollIntoView({ behavior: 'smooth' });
}

function handleWdQuestion(id) {
  let index = -1
  for (let i = 0; i < wdQuestionData.value.length; i++) {
    if (wdQuestionData.value[i].id == id) {
      index = i
      break
    }
  }
  let selectQuestion = wdQuestionData.value[index]
  insertForm.question = selectQuestion.question
  insertForm.answer = selectQuestion.answer
  if (selectQuestion.optionA.substring(0, 2) == 'A.') {
    insertForm.optionA = selectQuestion.optionA.substring(2, selectQuestion.optionA.length)
    insertForm.optionB = selectQuestion.optionB.substring(2, selectQuestion.optionB.length)
    insertForm.optionC = selectQuestion.optionC.substring(2, selectQuestion.optionC.length)
    insertForm.optionD = selectQuestion.optionD.substring(2, selectQuestion.optionD.length)
  }
  if (selectQuestion.optionA.substring(0, 1) == '.') {
    insertForm.optionA = selectQuestion.optionA.substring(1, selectQuestion.optionA.length)
    insertForm.optionB = selectQuestion.optionB.substring(1, selectQuestion.optionB.length)
    insertForm.optionC = selectQuestion.optionC.substring(1, selectQuestion.optionC.length)
    insertForm.optionD = selectQuestion.optionD.substring(1, selectQuestion.optionD.length)
  }
  else {
    insertForm.optionA = selectQuestion.optionA
    insertForm.optionB = selectQuestion.optionB
    insertForm.optionC = selectQuestion.optionC
    insertForm.optionD = selectQuestion.optionD
  }
  insertForm.subject = selectQuestion.subject
  insertForm.optionType = selectQuestion.optionType
  isWdDrawerOpen.value = false
  setTimeout(() => {
    handleOptionTypechange()
  }, 50);
}
function isWindowWidthGreaterThanHeight() {
  var windowWidth = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
  var windowHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
  return (windowWidth > windowHeight)
}
function handleToastConform() {
  isShowToast.value = false
  localStorage.setItem('isShowToast', true)
}
onMounted(() => {
  if (localStorage.getItem('isShowToast') == null) {
    isShowToast.value = true
  }
  isWindowWidthGreaterThanHeight1.value = isWindowWidthGreaterThanHeight()
  checkLogin()
  if (!isLogin.value) {
    let vtimer = setInterval(() => {
      if (loginForm.value.username != '' && loginForm.value.password != '') {
        clearInterval(vtimer)
        updateVerifyImg()
      }
    }, 100);
  }
  showAnswer.value = false
  document.addEventListener('fullscreenchange', (value) => {
    if (document.fullscreenElement)
      isInitFullScreen.value = false
  })
})
watch(selectedKeys, (newValue, oldValue) => {
  if (isLogin.value) {
    if (subjectsCount.value[subsx[selectedKeys.value - 1]] == 0)
      description.value = "暂无题目，快来添加吧！"
    else
      description.value = "恭喜！今日该科目的错题全部复习完成！"
    questionLoading.value = true
    question.value = ""
    isQuestionEmpty.value = false
    showAnswer.value = false
    globalData.value = true
    currentId.value = 0
    getQuestions()
  }
})
watch(wdCurrent, (newValue, oldValue) => {
  if (isLogin.value) {
    let mapper = { 'sjjg': '数据结构', 'czxt': '操作系统', 'jz': '计算机组成原理', 'jsjwl': '计算机网络' }
    wdQuestionData.value = getWdQuestions(mapper[newValue])
    lastWdSubject.value = newValue
  }
})
const chapterGroupedData = computed(() => {
  const grouped = {};
  try {
    wdQuestionData.value.forEach(question => {
      if (!grouped[question.chapter]) {
        grouped[question.chapter] = [];
      }
      const chapterQuestions = grouped[question.chapter];
      chapterQuestions.push(question);
      chapterQuestions.sort((a, b) => a.index - b.index);
    });
  } catch (error) {

  }
  return Object.entries(grouped);
});  
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
}

#app {
  width: 100vw;
  height: 100vh;
}

.layout {
  width: 100vw;
  height: 100vh;
}

.phoneMargin {
  padding: 0 10px;
}

.pcMargin {
  padding: 0 50px;
}

.contentContainer {
  position: relative;
  height: 93%;
  background: #fff;
  background-image: linear-gradient(156deg, #95b8dba8, #9b86e354);
  overflow: hidden;
  border-radius: 10px;
}

.contentDiv {
  position: absolute;
  left: 10%;
  top: 10%;
  width: 80%;
  height: 85%;
  border: 1px solid gray;
  box-shadow: 0.5px 0.5px 0.5px lightskyblue;
  border-radius: 15px;
  text-align: center;
  background: transparent;
}

.search {
  position: absolute;
  display: flex;
  right: 12%;
  top: 72px;
  z-index: 2;
  scale: 0.8;
}

.question {
  position: relative;
  width: 60%;
  left: 20%;
  margin-top: 0%;
  font-size: 1.6rem;
  text-align: left;
  user-select: none;
  -webkit-user-select: none;
  word-break: normal;
  max-height: 23vh;
  min-height: 23vh;
  overflow-y: scroll;
  overflow-x: hidden;
  border: 0.5px solid gray;
  border-radius: 10px;
  padding: 1%;
}

.questionWord {
  opacity: 0;
}

.answerWordA {
  opacity: 0;
}

.answerWordB {
  opacity: 0;
}

.answerWordC {
  opacity: 0;
}

.answerWordD {
  opacity: 0;
}

.showQuestion {
  animation: newQuestionShow 0.3s ease-in-out;
  animation-fill-mode: forwards
}

.showAnswer {
  animation: newQuestionShow 0.3s ease-in-out;
  animation-fill-mode: forwards
}

@keyframes newQuestionShow {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

.answer {
  position: absolute;
  min-width: 96%;
  height: 60px;
  bottom: 10%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 2%;
}

.xuanze {
  position: absolute;
  width: 45vw;
  left: 20%;
  height: 25vh;
  margin-top: 2%;
  padding: 1%;
  border-radius: 15px;
}

.choices {
  position: relative;
  width: 100%;
  height: 30%;
  margin-bottom: 10px;
  border: 2px solid;
  border-image: linear-gradient(120deg, #e79090 0%, #9bd3ec 100%) 1;
  display: flex;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
}

.choices:hover {
  transform: translateY(-5%);
}

.emptyContainer {
  position: absolute;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.spinContainer {
  position: absolute;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 5;
}

.next {
  position: absolute;
  right: 0;
  top: 40%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid black;
  border-radius: 50%;
  font-size: 24px;
  cursor: pointer;
  z-index: 2;
}

.last:hover,
.next:hover {
  box-shadow: 3px 3px 3px gray;
}

.last {
  position: absolute;
  left: 0;
  top: 40%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid black;
  border-radius: 50%;
  font-size: 24px;
  cursor: pointer;
  z-index: 2;
}

.explaination {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: right;
  font-size: 24px;
  z-index: 2;
  padding: 2px;
}

.questionIndex {
  position: absolute;
  left: 1px;
  top: -41px;
  width: auto;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  user-select: none;
  z-index: 2;
}

.insert {
  position: absolute;
  width: 500px;
  height: 60px;
  right: 15%;
  z-index: 3;
}

.update {
  position: absolute;
  width: 100px;
  height: 60px;
  right: 20%;
  z-index: 3;
}

.editInsert {
  background-color: transparent;
  width: 50%;
  margin-top: 10%;
}

#components-layout-demo-top .logo {
  float: left;
  width: 120px;
  height: 31px;
  background: rgba(255, 255, 255, 0.3);
}

.ant-row-rtl #components-layout-demo-top .logo {
  float: right;
}

[data-theme='dark'] .site-layout-content {
  background: #141414;
}


.insertMask {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
  background-color: transparent;
  opacity: 0.8;
}

.cropMask {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 11;
  background-color: transparent;
  opacity: 0.8;
}

.ant-select:not(.ant-select-customize-input) .ant-select-selector {
  background-color: transparent;
  border-color: gray;
}

.questionList {
  position: relative;
  width: 100%;
  height: auto;
  display: flex;
  flex-wrap: wrap;
}

.QTag {
  margin: 2%;
  padding: 1.5%;
  cursor: pointer;
  min-width: 35px;
  min-height: 35px;
  text-align: center;
}

.WTag {
  margin: 2%;
  padding: 1.5%;
  min-width: 35px;
  min-height: 35px;
  text-align: center;
  font-size: 18px;
}

.tagSelected {
  border-color: blue;
  pointer-events: none;
}

::-webkit-scrollbar {
  width: 0.8px;
  height: 60%;
}


::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 10px;
}


::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}


::-webkit-scrollbar-thumb:hover {
  background-color: rgba(0, 0, 0, 0.6);
}
</style>