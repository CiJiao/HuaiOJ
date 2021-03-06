<template>
  <Row>
    <Col span="22" offset="1" align="left" style="padding-top: 30px;">
      <Alert type="warning"
        >注意！管理员在测试比赛题目的时候请务必保持比赛状态为没有开始，这时只有管理员可以正常查看和提交题目，而且不会产生排名。</Alert
      >
      <div class="item-padding">
        <h3>比赛名称</h3>
        <Input v-model="name" placeholder="" style="width: 100%" />
      </div>
      <div class="item-padding">
        <h3>比赛介绍(不显示之前介绍，如不修改，不会改变)</h3>
        <vue-wangeditor
          style="text-align: left"
          id="editor"
          :options="options"
          v-model="description"
          :init-content="initContent"
          :text.sync="text"
        ></vue-wangeditor>
      </div>
      <div>
        <Row>
          <Col span="11">
            <div class="item-padding">
              <h3>开始时间</h3>
              <DatePicker
                type="datetime"
                placeholder="选择开始时间"
                v-model="startDate"
              ></DatePicker>
            </div>
          </Col>
          <Col span="11" offset="2">
            <div class="item-padding">
              <h3>结束时间</h3>
              <DatePicker
                type="datetime"
                placeholder="选择结束时间"
                v-model="endDate"
              ></DatePicker>
            </div>
          </Col>
          <Col span="11">
            <div class="item-padding">
              <h3>比赛类型</h3>
              <RadioGroup v-model="contestType">
                <Radio label="ICPC">ICPC</Radio>
                <Radio label="IO">OI</Radio>
              </RadioGroup>
            </div>
          </Col>
          <Col span="11" offset="2">
            <div class="item-padding">
              <h3>是否可见</h3>
              <RadioGroup v-model="visual">
                <Radio label="visible">可见</Radio>
                <Radio label="invisible">不可见</Radio>
              </RadioGroup>
            </div>
          </Col>
          <Col span="11">
            <div class="item-padding">
              <h3>是否允许分享</h3>
              <RadioGroup v-model="sharable">
                <Radio label="allow">允许</Radio>
                <Radio label="deny">不允许</Radio>
              </RadioGroup>
            </div>
          </Col>
          <Col span="11" offset="2">
            <div class="item-padding">
              <h3>延迟封榜(单位：小时)</h3>
              <InputNumber :max="100" :min="1" v-model="delay"></InputNumber>
            </div>
          </Col>
        </Row>
      </div>
      <div class="item-padding">
        <Row>
          <Col span="11" style="height:100px">
            <div class="item-padding">
              <h3>比赛类型</h3>
              <RadioGroup v-model="openType">
                <Radio label="PUBLIC">公开赛</Radio>
                <Radio label="SECRET_WITH_PASSWORD">私密赛（可加入）</Radio>
                <Radio label="SECRET_WITHOUT_PASSWORD"
                  >私密赛（不可加入）</Radio
                >
              </RadioGroup>
            </div>
          </Col>
          <Col v-if="openType === 'SECRET_WITH_PASSWORD'" span="11" offset="2">
            <div class="item-padding">
              <h3>邀请密码</h3>
              <Input
                v-model="password"
                placeholder="请输入密码"
                style="width: 100%"
              />
            </div>
          </Col>
        </Row>
        <Row>
          <Col span="2">
            <Button type="primary" @click="createContest">发布比赛</Button>
          </Col>
        </Row>
      </div>
    </Col>
  </Row>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import api from '../../api/api'
import md5 from 'js-md5'

@Component
export default class Admin extends Vue {
  // 文本编辑器配置
  text: any = ''
  options: any = {
    width: '100%',
    height: '100px',
  }

  initContent = ''
  // contest参数
  name: any = ''
  description: any = ''
  openType: string = 'PUBLIC'
  contestType: string = 'ICPC'
  sharable: string = 'allow'
  enable: any = false
  visual: string = 'visible'
  delay: number = 0
  startDate: any = ''
  endDate: any = ''
  password: any = ''

  computeDate(date: string) {
    const start = new Date(date)
    const year = start.getFullYear()
    const mon = start.getMonth() + 1
    const month = () => {
      if (mon < 10) {
        return '0' + mon
      } else {
        return mon
      }
    }
    const day = () => {
      if (start.getDate() < 10) {
        return '0' + start.getDate()
      } else {
        return start.getDate()
      }
    }
    const hour = () => {
      if (start.getHours() < 10) {
        return '0' + start.getHours()
      } else {
        return start.getHours()
      }
    }
    const min = () => {
      if (start.getMinutes() < 10) {
        return '0' + start.getMinutes()
      } else {
        return start.getMinutes()
      }
    }
    const second = () => {
      if (start.getSeconds() < 10) {
        return '0' + start.getSeconds()
      } else {
        return start.getSeconds()
      }
    }
    return (
      year +
      '-' +
      month() +
      '-' +
      day() +
      ' ' +
      hour() +
      ':' +
      min() +
      ':' +
      second()
    )
  }

  createContest() {
    const userInfo = this.$store.state.userInfo
    const start = this.computeDate(this.startDate)
    const end = this.computeDate(this.endDate)
    const pwd =
      this.openType === 'SECRET_WITH_PASSWORD' ? md5(this.password) : ''
    api
      .createContest({
        contestType: this.contestType,
        description: this.description,
        startDate: start,
        endDate: end,
        frozenOffset: this.delay * 3600000,
        name: this.name,
        openType: this.openType,
        password: pwd,
        sharable: this.sharable === 'allow',
        visible: this.visual === 'visible',
      })
      .then((res: any) => {
        ;(this as any).$Message.success('创建成功')
        this.name = ''
        this.initContent = ''
        this.description = ''
        this.openType = 'PUBLIC'
        this.visual = 'visible'
        this.startDate = ''
        this.endDate = ''
        this.password = ''
      })
      .catch((err: any) => {
        ;(this as any).$Message.error(err.data.message)
      })
  }
}
</script>

<style scoped>
.item-padding {
  padding: 20px 0 20px 0;
}
</style>
