<template>
  <div>
    <Row>
      <Col span="24" align="left" class="item-padding">
        <Button @click="backToContest()" style="margin-bottom: 4px">
          <Icon type="md-arrow-round-back" size="28" />返回
        </Button>
      </Col>
      <Col
        span="24"
        align="left"
        class="item-padding"
        style="margin-bottom: 40px"
      >
        <Tabs>
          <TabPane label="自动组卷" name="auto" style="padding: 0 20px">
            <Row>
              <Col span="12">
                <div id="positive"></div>
              </Col>
              <Col span="12">
                <div id="negative"></div>
              </Col>
              <Col span="24" style="text-align: left;">
                <div class="part">
                  <h2>需要的知识点:</h2>
                  <CheckboxGroup v-model="include" class="checkgroup">
                    <template v-for="(item, index) in tags">
                      <Checkbox :label="item.id" :key="item.id">{{
                        item.name
                      }}</Checkbox>
                    </template>
                  </CheckboxGroup>
                </div>
              </Col>
              <Col span="24" style="text-align: left;">
                <div class="part">
                  <h2>不需要的知识点:</h2>
                  <CheckboxGroup v-model="exclude" class="checkgroup">
                    <template v-for="(item, index) in tags">
                      <Checkbox :label="item.id" :key="item.id">{{
                        item.name
                      }}</Checkbox>
                    </template>
                  </CheckboxGroup>
                </div>
              </Col>
              <Col span="12" style="text-align: left;padding-top: 30px">
                <div class="part">
                  <h2>难度系数(0-10):</h2>
                  <InputNumber
                    :max="10"
                    :min="0"
                    :step="1"
                    v-model="difficulty"
                  ></InputNumber>
                </div>
                <div class="part">
                  <h2>题目数目:</h2>
                  <InputNumber
                    :max="10"
                    :min="0"
                    :step="1"
                    v-model="problemNum"
                  ></InputNumber>
                </div>
              </Col>
              <Col span="12" style="text-align: left;padding-top: 30px">
                <div class="part">
                  <h2>比赛时长（分钟）:</h2>
                  <InputNumber
                    :max="10000"
                    :min="0"
                    :step="1"
                    v-model="duration"
                  ></InputNumber>
                </div>
                <div class="part">
                  <h2>距离上次使用最短时间（天）:</h2>
                  <InputNumber
                    :max="10000"
                    :min="0"
                    :step="1"
                    v-model="interval"
                  ></InputNumber>
                </div>
              </Col>
              <Button type="primary" style="margin-top: 10px" @click="recommend"
                >进行推荐</Button
              >
              <Modal
                v-model="modal"
                title="推荐"
                width="80%"
                @on-ok="ok"
                @on-cancel="cancel"
              >
                <div v-if="pending" align="center">
                  <Loading />
                </div>
                <div v-else>
                  <Row
                    style="width: 100%;height: 48px;font-weight: 600;font-size: 18px"
                  >
                    <Col span="8" align="left">
                      <div>题目</div>
                    </Col>
                    <Col span="4" align="left">
                      <div>难度</div>
                    </Col>
                    <Col span="12" align="left">
                      <div>标签</div>
                    </Col>
                  </Row>
                  <CheckboxGroup v-model="choose" class="checkgroup">
                    <Row style="padding-bottom: 20px;font-size: 18px">
                      <div
                        v-for="(item, index) in recommendList"
                        style="width: 100%;height: 48px"
                      >
                        <Col span="8" align="left">
                          <Checkbox :label="item.id" :key="item.id">
                            <span style="width: 20vw">{{ item.title }}</span>
                          </Checkbox>
                        </Col>
                        <Col span="4" align="left">{{ item.difficulty }}</Col>
                        <Col span="12" align="left">
                          <template v-for="i in item.tagList">
                            <span
                              style="height: 20px;padding: 2px 6px; border: 1px solid #2d8cf0;border-radius: 6px;color:#2d8cf0;margin: 6px 4px; "
                              >{{ i.name }}</span
                            >
                          </template>
                        </Col>
                      </div>
                    </Row>
                  </CheckboxGroup>
                </div>
              </Modal>
            </Row>
          </TabPane>
          <TabPane label="手动组卷" name="hand">
            <Row style="text-align: left;padding-top: 40px">
              <Col span="24">
                <h2>用题号添加：</h2>
              </Col>
              <Col span="14">
                <AutoComplete
                  search
                  v-model="addById"
                  icon="ios-search"
                  placeholder="请输入题号"
                  @on-change="getProblemsByTitle"
                  @on-select="pushInto"
                  style="width: 100%;"
                  placement="bottom"
                >
                  <Option
                    v-for="(item, index) in searchData"
                    :value="item.idx"
                    :key="index"
                  >
                    <div class="option-two">
                      <span>{{ item.title }}</span>
                    </div>
                  </Option>
                </AutoComplete>
              </Col>
            </Row>
          </TabPane>
        </Tabs>
      </Col>
      <Col
        span="24"
        align="left"
        style="padding: 40px 0px;border-top: 1px solid #e9e9e9;"
      >
        <h1>已添加题目</h1>
        <Row
          class="list"
          style="color: #17233d;font-size: 14px;font-weight: 500;padding-bottom: 10px"
        >
          <Col span="2">
            <p>题号</p>
          </Col>
          <Col span="8">
            <p>题目</p>
          </Col>
          <Col span="3">
            <p>上次使用时间</p>
          </Col>
          <Col span="4">
            <p>通过次数/提交总数</p>
          </Col>
          <Col span="3" v-if="judgeType === 'DELAY'">
            <p>分数</p>
          </Col>
          <Col span="4" style="text-align: center">
            <p>操作</p>
          </Col>
        </Row>
        <Row v-for="(item, index) in problems" class="item" :key="item.id">
          <Col span="2">
            <p>{{ item.label }}</p>
          </Col>
          <Col span="8">
            <a @click="toProblem(item.id)">{{ item.title }}</a>
          </Col>
          <Col span="3">
            <p>{{ item.lastUsedDate || '未使用过' }}</p>
          </Col>
          <Col span="4">
            <p>
              {{
                Math.floor(item.acceptRate * 100) +
                  '%(' +
                  String(item.acceptCount) +
                  ' / ' +
                  String(item.submitCount) +
                  ')'
              }}
            </p>
          </Col>
          <Col span="3" v-if="judgeType === 'DELAY'">
            <p>{{ item.score }}</p>
          </Col>
          <Col span="4" style="text-align: center">
            <Button
              type="text"
              style="color: #2d8cf0"
              @click="deleteProblem(item.id)"
              >删除</Button
            >
          </Col>
        </Row>
      </Col>
      <Modal
        v-model="detailModal"
        :title="problemDetail.title"
        width="70%"
        @on-ok="setProblemsToContest"
        @on-cancel="cancel"
      >
        <div class="wrapper">
          <div class="problem-section">
            <h3>描述</h3>
            <p v-html="problemDetail.description"></p>
          </div>
          <div class="problem-section">
            <h3>输入</h3>
            <p v-html="problemDetail.inputDesc"></p>
          </div>
          <div class="problem-section">
            <h3>输出</h3>
            <p v-html="problemDetail.outputDesc"></p>
          </div>
          <div class="problem-section">
            <h3>难度</h3>
            <p v-html="problemDetail.difficulty"></p>
          </div>
          <div class="problem-section">
            <h3>详细数据</h3>
            <p>
              {{
                Math.floor(problemDetail.acceptRate * 100) +
                  '%(' +
                  String(problemDetail.acceptCount) +
                  ' / ' +
                  String(problemDetail.submitCount) +
                  ')'
              }}
            </p>
          </div>
          <div class="problem-section" style="clear: both">
            <h3>标签</h3>
            <p>
              <Tag
                type="dot"
                v-for="(tag, index) in problemDetail.tagList"
                :key="index"
                v-if="problemDetail.tagList.length > 0"
                color="success"
                >{{ tag.name }}</Tag
              >
              <span v-else>无</span>
            </p>
          </div>
          <div class="problem-section" style="clear: both">
            <h3>样例</h3>
            <div
              style="padding-bottom: 10px;"
              type="dot"
              v-for="(item, index) in problemDetail.sample"
              :key="index"
              v-if="problemDetail.sample.length > 0"
            >
              <div>Input : {{ item.input }}</div>
              <div>Output : {{ item.input }}</div>
            </div>
            <span v-else>无</span>
          </div>
          <div
            class="problem-section"
            style="clear: both"
            v-if="judgeType === 'DELAY'"
          >
            <h3>设置分数</h3>
            <InputNumber
              :max="100"
              :min="10"
              :step="10"
              v-model="score"
            ></InputNumber>
          </div>
        </div>
      </Modal>
    </Row>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import api from '../../api/api'
import Loading from '../../components/Loading.vue'
import echarts from 'echarts'

@Component({
  components: { Loading },
})
export default class Admin extends Vue {
  detailModal: boolean = false
  setProblem: any = {}
  alphabet: any = [
    'A',
    'B',
    'C',
    'D',
    'E',
    'F',
    'G',
    'H',
    'I',
    'J',
    'K',
    'L',
    'M',
    'N',
    'O',
    'P',
    'Q',
    'R',
    'S',
    'T',
    'U',
    'V',
    'W',
    'X',
    'Y',
    'Z',
  ]
  searchData: any = []
  problems: any = []
  addById: string = ''
  tags: any = []
  include: Array<string> = []
  exclude: Array<string> = []
  choose: Array<string> = []
  difficulty: Number = 5
  problemNum: Number = 5
  interval: Number = 20
  duration: Number = 60
  userList: Array<string> = []
  recommendList: Array<string> = []
  modal: boolean = false
  pending: boolean = true
  score: number = 10
  judgeType: string = ''
  problemDetail: any = {
    id: '',
    title: '',
    description: '',
    inputDesc: '',
    outputDesc: '',
    difficulty: '',
    acceptRate: '',
    acceptCount: '',
    submitCount: '',
    tagList: [],
    sample: {},
    sampleIO: '',
  }
  negativeOption = {
    title: {
      text: '题目类别综合掌握度Bottom5',
    },
    dataset: {
      source: [
        ['score', '分数', '考点'],
        [-20, -20, '拓扑排序'],
        [-63, -63, '树状数组'],
        [-125, -125, '回溯算法'],
        [-188, -188, '几何'],
        [-320, -320, '贪心算法'],
      ],
    },
    grid: { containLabel: true },
    xAxis: { name: 'amount' },
    yAxis: { type: 'category' },
    visualMap: {
      orient: 'horizontal',
      left: 'center',
      min: -40,
      max: 0,
      text: ['掌握度高', '掌握度低'],
      dimension: 0,
      inRange: {
        color: ['#a39aaf', '#463f4e'],
      },
    },
    series: [
      {
        type: 'bar',
        encode: {
          x: 'amount',
          y: '考点',
        },
      },
    ],
  }

  positiveOption = {
    title: {
      text: '题目类别综合掌握度Top5',
    },
    dataset: {
      source: [
        ['score', '分数', '考点'],
        [24, 24, '树'],
        [62, 62, '二分查找'],
        [125, 125, '双指针'],
        [186, 186, '排序'],
        [328, 328, '链表'],
      ],
    },
    grid: { containLabel: true },
    xAxis: { name: '分数' },
    yAxis: { type: 'category' },
    visualMap: {
      orient: 'horizontal',
      left: 'center',
      dimension: 0,
      min: 0,
      max: 40,
      text: ['掌握度高', '掌握度低'],
      inRange: {
        color: ['#a39aaf', '#463f4e'],
      },
    },
    series: [
      {
        type: 'bar',
        encode: {
          x: '分数',
          y: '考点',
        },
      },
    ],
  }

  getJudgeType() {
    const params = this.$route.params
    const id: string = params.id
    this.judgeType = this.$store.state.contestList.filter((item: any) => {
      return item.id === id
    })[0].judgeType
  }

  getAllProblemsFromASpecificContest() {
    const params = this.$route.params
    const id: string = params.id
    const that = this
    that.problems = []
    api
      .getAllProblemsFromASpecificContest({ id })
      .then((res: any) => {
        this.problems = res.data
      })
      .catch((err: any) => {
        ;(this as any).$Message.error(err.data.message)
      })
  }

  ok() {
    const params = this.$route.params
    const contestId: any = params.id
    api
      .setProblemsToContest({
        id: contestId,
        problemId: this.choose,
      })
      .then((res: any) => {
        ;(this as any).$Message.success('添加成功')
        this.getAllProblemsFromASpecificContest()
      })
      .catch((err: any) => {
        ;(this as any).$Message.error(err.data.message)
      })
  }

  cancel() {
    this.detailModal = false
  }

  recommend() {
    this.pending = true
    api
      .getProblems({
        page: 0,
        size: 10,
        visible: true,
      })
      .then((res: any) => {
        setTimeout(() => {
          this.pending = false
          this.recommendList = res.data.list
        }, 4000)
      })
      .catch((err: any) => {
        console.log(err)
      })
    this.modal = true
    // api
    //   .getRecommend({
    //     difficultDegree: this.difficulty,
    //     count: this.problemNum,
    //     duration: this.duration || 0,
    //     interval: this.interval,
    //     tagIdsInclude: this.include,
    //     tagIdsExclude: this.exclude,
    //     userIdList: this.userList,
    //   })
    //   .then((res: any) => {
    //     this.pending = false
    //     this.recommendList = res.data
    //   })
    //   .catch((err: any) => {
    //     ;(this as any).$Message.error(err.data.message)
    //   })
  }

  backToContest() {
    this.$router.push({
      path: `/admin/contests-list`,
    })
  }

  toProblem(id: string) {
    this.$router.push({
      path: `/problems/${id}`,
    })
  }

  getProblemsByTitle() {
    if (this.addById !== '') {
      if (parseInt(this.addById) > 0) {
        api
          .getProblemsIdByTitle({
            idx: this.addById,
          })
          .then((res: any) => {
            if (res.data.list.length > 0) {
              this.searchData = res.data.list
            } else {
              ;(this as any).$Message.info('没有搜索到相关题目')
            }
          })
          .catch((err: any) => {
            ;(this as any).$Message.error(err.data.message)
          })
      } else {
        ;(this as any).$Message.warning('请输入题号哦')
      }
    }
  }

  pushInto(idx: any) {
    const problem = this.searchData.filter((item: any) => item.idx === idx)[0]
    this.problemDetail = { ...problem }
    if (this.problemDetail.hasOwnProperty('sampleIO')) {
      this.problemDetail.sample = JSON.parse(problem.sampleIO)
    } else {
      this.problemDetail.sample = []
    }
    this.detailModal = true
  }

  setProblemsToContest() {
    this.addById = ''
    const params = this.$route.params
    const contestId: any = params.id
    const problemId = this.problemDetail.id
    if (this.judgeType === 'DELAY') {
      api
        .setProblemScore({
          id: contestId,
          problemId: problemId,
          score: this.score,
        })
        .then(() => {
          this.getAllProblemsFromASpecificContest()
          ;(this as any).$Message.success('添加成功')
        })
        .catch((err: any) => {
          ;(this as any).$Message.error(err.data.message)
        })
    } else {
      api
        .setProblemsToContest({
          id: contestId,
          problemId: [problemId],
        })
        .then((res: any) => {
          this.getAllProblemsFromASpecificContest()
          ;(this as any).$Message.success('添加成功')
        })
        .catch((err: any) => {
          ;(this as any).$Message.error(err.data.message)
        })
    }
  }

  getTagId() {
    api
      .getTags()
      .then((res: any) => {
        this.tags = res.data
      })
      .catch((err: any) => {
        ;(this as any).$Message.error(err.data.message)
      })
  }
  deleteProblem(itemId: any) {
    const params = this.$route.params
    const id: any = params.id
    const idArray: Array<number> = []
    idArray.push(itemId)
    api
      .deleteProblemsInContest({
        id: id,
        problemArray: idArray,
      })
      .then((res: any) => {
        ;(this as any).$Message.success('删除成功')
        this.getAllProblemsFromASpecificContest()
      })
      .catch((err: any) => {
        ;(this as any).$Message.error(err.data.message)
      })
  }

  getUser() {
    const params = this.$route.params
    const id: any = params.id
    api
      .getAllUserFromContest({ id })
      .then((res: any) => {
        res.data.forEach((item: any) => {
          this.userList.push(item.id)
        })
      })
      .catch((err: any) => {
        ;(this as any).$Message.error(err.data.message)
      })
  }

  mounted() {
    const positive = echarts.init(document.getElementById('positive'))
    positive.setOption(this.positiveOption)
    const negative = echarts.init(document.getElementById('negative'))
    negative.setOption(this.negativeOption)
    if (this.$store.state.contestList.length === 0) {
      this.$router.push({
        path: `/admin/contests-list`,
      })
    } else {
      this.getTagId()
      this.getUser()
      this.getAllProblemsFromASpecificContest()
      this.getJudgeType()
    }
  }
}
</script>

<style lang="less" scoped>
.item-padding {
  padding: 30px 0 0 0;
}

.list {
  margin-top: 40px;
  text-align: left;
  color: #17233d;
  font-weight: 600;
  border-bottom: 1px solid #e8eaec;
  padding-bottom: 10px;
}

.item {
  text-align: left;
  border-bottom: 1px solid #e8eaec;
  padding-top: 10px;
}

.part {
  padding-top: 30px;
  padding-bottom: 30px;
}

.title {
  font-weight: bold;
}
.row-height {
  height: 42px;
  line-height: 41px;
  border-top: 1px solid rgb(221, 221, 221);
  &:nth-child(odd) {
    background-color: #f9f9f9;
  }
}

.last-row {
  margin-top: 24px;
}
.problem-section {
  padding: 5px 0;
  width: 50%;
  p {
    text-align: left;
    font-size: 15px;
    color: rgb(51, 51, 51);
    margin-bottom: 10px;
  }
  pre {
    background-color: white;
    white-space: pre-wrap;
    word-wrap: break-word;
    padding: 9px;
    margin: 2px 0 9px;
    font-size: 12px;
    line-height: 1.4;
    border-radius: 0;
    border: 1px solid #ccc;
    text-align: left;
  }
}
.wrapper {
  display: flex;
  flex-flow: wrap;
}
#positive {
  width: 100%;
  height: 400px;
}
#negative {
  width: 100%;
  height: 400px;
}
</style>
