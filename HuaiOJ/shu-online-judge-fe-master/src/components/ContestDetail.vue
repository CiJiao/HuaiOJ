<template>
  <Row>
    <Col span="22" offset="1">
      <Modal
        v-model="modalPassword"
        title="密码验证"
        @on-ok="sendPassword"
        @on-cancel="cancel"
      >
        <h2>如要加入，请输入密码</h2>
        <Input
          v-model="password"
          type="password"
          placeholder="输入比赛加入密码："
          style="width: 200px"
        />
      </Modal>
      <Modal
        v-model="modal"
        :title="modalTitle"
        @on-ok="cancel"
        @on-cancel="cancel"
      >
        <p>{{ modalContent }}</p>
      </Modal>
      <div class="timeInterval" v-if="contestStatus === '正在进行'">
        还剩{{ interval }}比赛结束
      </div>
      <Tabs
        value="detail"
        style="padding-top: 40px;"
        v-on:on-click="onTabChange"
      >
        <TabPane label="比赛详情" name="detail" class="pane-padding">
          <h1>{{ contest.name }}</h1>
          <h3 style="text-align: left;font-weight: 600;">描述:</h3>
          <p class="description" v-html="contest.description"></p>
          <Table stripe :columns="columns" :data="data"></Table>
        </TabPane>
        <TabPane label="题目列表" name="problem" class="pane-padding">
          <Table :columns="title" :data="problems"></Table>
        </TabPane>
        <TabPane
          label="排名"
          name="rank"
          style="text-align: left"
          class="pane-padding"
        >
          <Row>
            <Col span="8">
              <span style="font-weight: 500;margin: 0 10px 0 10px;"
                >自动刷新：</span
              >
              <i-switch v-model="autoRefresh" @on-change="refreshAuto" />
            </Col>
            <Col span="6">
              <AutoComplete
                v-if="show"
                v-model="author"
                :data="authorSearch"
                clearable
                placeholder="搜索某老师创建群组的成员成绩"
                search
                icon="ios-search"
                @on-change="getAuthorByName"
                @on-select="selectedAuthor"
                placement="bottom"
                style="width:280px"
              >
                <Option
                  v-for="(item, index) in authorSearch"
                  :value="item.id"
                  :key="index"
                >
                  <div class="option-two">
                    <span>{{ item.name }}</span>
                  </div>
                </Option>
              </AutoComplete>
              <AutoComplete
                v-if="show"
                v-model="author"
                :data="authorSearch"
                clearable
                placeholder="搜索某老师创建群组的成员成绩"
                search
                icon="ios-search"
                @on-change="getAuthorByName"
                @on-select="selectedAuthor"
                placement="bottom"
                style="width:280px;opacity: 0;position: absolute"
              >
                <Option
                  v-for="(item, index) in authorSearch"
                  :value="item.id"
                  :key="index"
                >
                  <div class="option-two">
                    <span>{{ item.name }}</span>
                  </div>
                </Option>
              </AutoComplete>
            </Col>
            <Col span="6">
              <AutoComplete
                v-if="show"
                v-model="name"
                :data="groupSearch"
                placeholder="以小组筛选显示排名"
                search
                clearable
                autocomplete="off"
                icon="ios-search"
                @on-change="getGroupsByName"
                @on-select="selectedGroup"
                placement="bottom"
                style="width:200px"
              >
                <Option
                  v-for="(item, index) in groupSearch"
                  :value="item.id"
                  :key="item.name"
                >
                  <div class="option-two">
                    <span>{{ item.name }}</span>
                  </div>
                </Option>
              </AutoComplete>
              <AutoComplete
                v-if="show"
                v-model="name"
                :data="groupSearch"
                placeholder="以小组筛选显示排名"
                search
                clearable
                icon="ios-search"
                @on-change="getGroupsByName"
                @on-select="selectedGroup"
                placement="bottom"
                style="width:200px;opacity: 0;position: absolute"
              >
                <Option
                  v-for="item in groupSearch"
                  :value="item.id"
                  :key="item.id"
                >
                  <div class="option-two">
                    <span>{{ item.name }}</span>
                  </div>
                </Option>
              </AutoComplete>
            </Col>
            <Col span="4">
              <Button type="primary" @click="exportRank">导出</Button>
            </Col>
          </Row>
          <table style="margin-top: 20px;">
            <tbody>
              <tr class="first-title">
                <td style="width:30px;">#</td>
                <td style="width:100px;">用户名</td>
                <td style="width:100px;">AC/总提交</td>
                <td
                  style="width:100px;"
                  v-if="contest.judgeType === 'IMMEDIATELY'"
                >
                  用时 + 罚时
                </td>
                <td style="width:100px;" v-else>总分</td>
                <td
                  v-for="item in problemKey"
                  :key="item"
                  style="width: 100px;"
                >
                  {{ item }}
                </td>
              </tr>
              <tr
                class="second-title"
                v-for="(user, index) in ranking"
                :key="index"
              >
                <td>{{ index + 1 }}</td>
                <td>{{ user.userName }}</td>
                <td>{{ user.acceptCount }}/{{ user.submitCount }}</td>
                <td v-if="contest.judgeType === 'IMMEDIATELY'">
                  <p v-html="timetrans(user.time)"></p>
                  <p v-if="user.errorCount !== 0">(-{{ user.errorCount }})</p>
                </td>
                <td v-else>
                  <p>{{ user.score }}</p>
                </td>
                <!--<template v-for="key in problemKey">-->
                <template v-for="(problem, key) in user.timeList">
                  <template v-if="contest.judgeType === 'IMMEDIATELY'">
                    <template v-if="problem.submitted === true">
                      <td
                        v-if="problem.passed === true"
                        :class="choose(problem.firstPassed)"
                      >
                        <div>{{ timetrans(problem.totalTime) }}</div>
                        <div v-if="problem.errorCount !== 0">
                          (-{{ problem.errorCount }})
                        </div>
                      </td>
                      <td v-else class="red">
                        <p v-html="timetrans(problem.totalTime)"></p>
                        <p v-if="problem.errorCount !== 0">
                          (-{{ problem.errorCount }})
                        </p>
                      </td>
                    </template>
                    <td v-else class="not-submitted"></td>
                  </template>
                  <template v-else>
                    <td :class="problem.submitted ? 'green' : 'red'">
                      <p>{{ problem.score }}</p>
                    </td>
                  </template>
                </template>
              </tr>
            </tbody>
          </table>
        </TabPane>
        <TabPane
          label="提交"
          name="submit"
          v-if="role !== 'ROLE_USER'"
          style="text-align: left"
          class="pane-padding"
        >
          <Row> </Row>
          <Table :columns="titleSubmission" :data="status"></Table>
          <div style="text-align: center;padding-top: 30px;">
            <Page
              :total="total"
              show-sizer
              @on-change="pageChange"
              @on-page-size-change="pageSizeChange"
            />
          </div>
        </TabPane>
      </Tabs>
    </Col>
  </Row>
</template>

<script lang="ts">
import { Vue, Component, Watch } from 'vue-property-decorator'
import api from '@/api/api'
import md5 from 'js-md5'
import { UserRole } from '../types/user'
import { RankingQuery } from '../types/ranking'
import axios from 'axios'
import { countInterval } from '@/util/util'
@Component
export default class ContestDetail extends Vue {
  show: boolean = false
  modalPassword: boolean = false
  modal: boolean = false
  modalTitle: string = ''
  modalContent: string = ''
  password: string = ''
  problemKey: Array<String> = []
  index = 0
  contest: any = {}
  columns: any = [
    {
      title: '开始时间',
      key: 'start',
    },
    {
      title: '结束时间',
      key: 'end',
    },
    {
      title: '状态',
      key: 'status',
    },
    {
      title: '比赛类型',
      key: 'type',
    },
    {
      title: '创建者',
      key: 'name',
    },
  ]
  contestStatus: string = '未进行'
  data: any = []
  title: any = [
    {
      title: 'ID',
      key: 'id',
      render: (h: any, obj: any) => {
        return h('span', obj.row.label)
      },
      width: 80,
    },
    {
      title: '题目名称',
      key: 'problemTitle',
      className: 'pointer-class',
      render: (h: any, obj: any) => {
        return h(
          'span',
          {
            on: {
              click: () => {
                this.toContestProblem(obj.row.id)
              },
            },
          },
          obj.row.title
        )
      },
    },
    {
      title: '通过率',
      key: 'authorName',
      className: 'pointer-class',
      render: (h: any, obj: any) => {
        return h(
          'span',
          Math.floor(obj.row.acceptRate * 100) +
            '%(' +
            String(obj.row.acceptCount) +
            ' / ' +
            String(obj.row.submitCount) +
            ')'
        )
      },
    },
  ]
  problems: any = []
  problemDetail: Array<any> = []
  autoRefresh: any = false
  ranking: Array<any> = []

  name: string = ''
  author: string = ''
  groupSearch: Array<any> = []
  authorSearch: Array<any> = []
  groupSelect: string = ''
  authorSelect: string = ''

  page: number = 0
  pageSize: number = 0
  total: number = 0
  endDate: number = 0
  interval: string = ''

  titleSubmission: any = [
    {
      title: '#',
      type: 'index',
      width: 78,
    },
    {
      title: '用户',
      key: 'authorName',
      className: 'pointer-class',
    },
    {
      title: '题目名称',
      key: 'problemTitle',
      className: 'pointer-class',
      render: (h: any, obj: any) => {
        return h(
          'span',
          {
            on: {
              click: () => {
                this.toContestProblemSubmission(obj.row.id)
              },
            },
          },
          obj.row.problemTitle
        )
      },
    },
    {
      title: '提交时间',
      key: 'createDate',
    },
    {
      title: '语言',
      key: 'language',
      filters: [
        {
          label: 'C',
          value: 1,
        },
        {
          label: 'CPP',
          value: 2,
        },
        {
          label: 'JAVA',
          value: 3,
        },
      ],
      filterMultiple: false,
      filterMethod(value: any, row: any) {
        if (value === 1) {
          return row.language === 'C'
        } else if (value === 2) {
          return row.language === 'CPP'
        } else if (value === 3) {
          return row.language === 'JAVA'
        }
      },
    },
    {
      title: '运行时间',
      key: 'duration',
      render: (h: any, obj: any) => {
        return h('span', (obj.row.duration || 0) + ' ms')
      },
    },
    {
      title: '结果',
      key: 'result',
      width: 350,
      align: 'center',
      filters: [
        {
          label: 'Accepted',
          value: 1,
        },
        {
          label: 'Wrong Answer',
          value: 2,
        },
        {
          label: 'Runtime Error',
          value: 3,
        },
        {
          label: 'Time Limit Exceeded',
          value: 4,
        },
        {
          label: 'Memory Limit Exceeded',
          value: 5,
        },
        {
          label: 'Compile Error',
          value: 6,
        },
        {
          label: 'Format Error',
          value: 7,
        },
      ],
      filterMultiple: false,
      filterMethod(value: any, row: any) {
        if (value === 1) {
          return row.result === 'Accepted'
        } else if (value === 2) {
          return row.result === 'Wrong Answer'
        } else if (value === 3) {
          return row.result === 'Runtime Error'
        } else if (value === 4) {
          return row.result === 'Time Limit Exceeded'
        } else if (value === 5) {
          return row.result === 'Memory Limit Exceeded'
        } else if (value === 6) {
          return row.result === 'Compile Error'
        } else if (value === 7) {
          return row.result === 'Format Error'
        }
      },
    },
  ]
  status: any = []

  @Watch('name')
  handleName(name: string) {
    if (name === '' && this.author === '') {
      this.getContestRanking()
    }
  }
  @Watch('author')
  handleAuthor(name: string) {
    if (name === '' && this.name === '') {
      this.getContestRanking()
    }
  }

  get role() {
    return this.$store.state.userInfo.authorities[0].authority
  }

  getGroupsByName(name: string) {
    this.authorSelect = ''
    this.author = ''
    api
      .getGroups({ name })
      .then(res => {
        this.groupSearch = res.data.list
      })
      .catch((err: any) => {
        ;(this as any).$Message.error(err.data.message)
      })
  }

  selectedGroup(id: any) {
    this.groupSelect = id
    this.getContestRanking({
      groupId: this.groupSelect,
      // teacherId: this.authorSelect,
    })
  }

  getAuthorByName() {
    api
      .getUser({
        role: UserRole.STUFF,
      })
      .then(res => {
        this.authorSearch = res.data.list
      })
      .catch((err: any) => {
        ;(this as any).$Message.error(err.data.message)
      })
  }

  selectedAuthor(id: any) {
    this.authorSelect = id
    this.getContestRanking({
      // groupId: this.groupSelect,
      teacherId: this.authorSelect,
    })
  }

  timetrans(time: string) {
    const date = new Date(time)
    const h =
      date.getHours() - 8 < 10
        ? '0' + (date.getHours() - 8)
        : date.getHours() - 8
    const m =
      date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()
    const s =
      date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds()
    return h + ':' + m + ':' + s
  }

  onTabChange(name: any) {
    switch (name) {
      case 'rank':
        this.getContestRanking()
        break
      case 'problem':
        this.getContestProblems()
        break
      case 'submit':
        this.getAllSubmissionOfContest()
      default:
        break
    }
  }

  pageChange(pages: number) {
    this.page = pages - 1
    this.getAllSubmissionOfContest(pages - 1, this.pageSize)
  }

  pageSizeChange(size: number) {
    this.getAllSubmissionOfContest(this.page, size)
    this.pageSize = size
  }

  toContestProblemSubmission(id: string) {
    this.$router.push({
      path: `/share/${id}`,
    })
  }

  getAllSubmissionOfContest(page: number = 0, pageSize: number = 10) {
    const params = this.$route.params
    const id: string = params.id
    api
      .getAllSubmissionOfContest({
        contestId: id,
        page: page,
        size: pageSize,
      })
      .then(res => {
        this.total = res.data.total
        this.status = res.data.list
      })
  }

  choose(status: boolean) {
    if (status === true) {
      return 'deepGreen'
    } else {
      return 'green'
    }
  }

  refreshAuto(status: any) {
    if (status === true) {
      ;(this as any).$Message.success('自动刷新开')
    } else {
      ;(this as any).$Message.success('自动刷新关')
    }
  }

  findStatus(value: any) {
    let status = ''
    if (value === 'NOT_STARTED') {
      status = '未开始'
    } else if (value === 'PROCESSING') {
      status = '正在进行'
    } else {
      status = '已经结束'
    }
    return status
  }

  findType(t: string) {
    let type = ''
    if (t === 'PUBLIC') {
      type = '公开赛'
    } else if (t === 'SECRET_WITH_PASSWORD') {
      type = '私密赛(可加入)'
    } else if (t === 'SECRET_WITHOUT_PASSWORD') {
      type = '私密赛(不可加入)'
    }
    return type
  }

  getContestDetail() {
    const params = this.$route.params
    const id: any = params.id
    api
      .getContestDetail({ id })
      .then((res: any) => {
        this.$store.state.currentContest = res.data
        this.contest = res.data
        this.contestStatus = this.findStatus(res.data.status)
        const t = res.data.openType
        const type = this.findType(t)
        this.endDate = res.data.endDate
        this.data.push({
          start: res.data.startDate,
          end: res.data.endDate,
          status: this.contestStatus,
          type: type,
          name: res.data.authorName,
        })
        const ed = new Date(Date.parse(res.data.endDate.replace(/-/g, '/')))
        setInterval(() => {
          const now = new Date()
          this.interval = countInterval(now, ed)
        }, 1000)
      })
      .catch((err: any) => {
        ;(this as any).$Message.error(err.data.message)
        if (err.status === 400) {
          switch (err.data.code) {
            // 比赛未开始
            case -2:
              this.modal = true
              this.modalTitle = '未开始'
              this.modalContent = '比赛还未开始，无权查看比赛消息'
              break
            // 没有这个比赛
            case -3:
              this.modal = true
              this.modalTitle = '没有这个比赛'
              this.modalContent = '你似乎进入了未知的区域！'
              break
            // 比赛不可加入
            case -4:
              this.modal = true
              this.modalTitle = '无权限'
              this.modalContent =
                '不好意思，此比赛是私密不可加入的呢！快去加入别的比赛吧！'
              break
            // 需要密码
            case -5:
              this.modalPassword = true
              break
            default:
              break
          }
        }
      })
  }

  getContestProblems() {
    this.problems = []
    const params = this.$route.params
    const id: string = params.id
    const that = this
    api
      .getAllProblemsFromASpecificContest({ id })
      .then((res: any) => {
        let index = -1
        that.problems = res.data.map((item: any) => {
          index = index + 1
          item.index = index
          return {
            ...item,
            index: index,
          }
        })
      })
      .catch((error: any) => {
        console.log(error)
      })
  }

  toContestProblem(problemId: string) {
    const params = this.$route.params
    const id: string = params.id
    this.$router.push({
      path: `/contests/${id}/problems/${problemId}`,
    })
  }

  sendPassword() {
    const pwd = md5(this.password)
    const params = this.$route.params
    const id: string = params.id
    api
      .sendPassword({
        id: id,
        password: pwd,
      })
      .then((res: any) => {
        if (res.data.message === '密码错误') {
          ;(this as any).$Message.error('密码错误')
          this.password = ''
          this.getContestDetail()
        } else if (res.data.message === '加入成功') {
          this.password = ''
          this.getContestDetail()
        }
      })
      .catch((err: any) => {
        ;(this as any).$Message.error(err.data.message)
        this.cancel()
      })
  }

  cancel() {
    this.$router.push({
      path: '/contests',
    })
  }

  download(data: any) {
    if (!data) {
      return
    }
    let url = window.URL.createObjectURL(
      new Blob([data], {
        type: 'application/octet-stream',
      })
    )
    let link = document.createElement('a')
    link.style.display = 'none'
    link.href = url
    link.setAttribute('download', 'ranking.xlsx')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }

  exportRank() {
    const params = this.$route.params
    const id: string = params.id
    const rankParams = {
      groupId: this.groupSelect,
      teacherId: this.authorSelect,
    }
    axios({
      url: `/api/v1/contests/${id}/ranking/export`,
      method: 'get',
      responseType: 'arraybuffer',
      params: rankParams,
    }).then(res => {
      this.download(res.data)
    })
  }

  getContestRanking(query?: RankingQuery) {
    const params = this.$route.params
    const id: string = params.id
    api
      .getRanking(id, query)
      .then((res: any) => {
        if (res.data.rankingUserList.length > 0) {
          this.problemKey = Object.keys(res.data.rankingUserList[0].timeList)
          this.ranking = res.data.rankingUserList
        } else {
          this.problemKey = []
          this.ranking = []
        }
      })
      .catch((err: any) => {
        ;(this as any).$Message.error(err.data.message)
      })
  }

  mounted() {
    this.getContestDetail()
    setTimeout(() => {
      this.show = true
    }, 1)
  }
}
</script>

<style lang="less" scoped>
.pane-padding {
  padding-top: 30px;
}

h1 {
  font-size: 30px;
  font-weight: 400;
  margin-bottom: 20px;
}

.description {
  text-align: left;
  padding: 20px 0 40px 0;
}

.red {
  color: #a94442;
  background-color: #f2dede;
}

.deepGreen {
  background-color: #3c9;
  color: #3c763d;
}

.green {
  color: #3c763d;
  background-color: #dff0d8;
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
  padding-bottom: 10px;
}

table {
  border-collapse: collapse;
}

td,
tr {
  text-align: center !important;
}

.first-title {
  width: 100%;
  height: 40px;
  white-space: nowrap;
  overflow: hidden;
  background-color: #f8f8f9;
  td {
    min-width: 0;
    height: 48px;
    box-sizing: border-box;
    text-overflow: ellipsis;
    vertical-align: middle;
    border: 1px solid #e8eaec;
  }
}

.second-title {
  width: 100%;
  height: 40px;
  white-space: nowrap;
  overflow: hidden;
  td {
    min-width: 0;
    height: 48px;
    box-sizing: border-box;
    text-align: left;
    text-overflow: ellipsis;
    vertical-align: middle;
    border: 1px solid #e8eaec;
  }
}

.timeInterval {
  padding-top: 30px;
  font-size: 16px;
}
</style>
