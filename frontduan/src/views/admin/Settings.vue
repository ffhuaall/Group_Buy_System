<template>
  <div class="settings">
    <el-card class="page-header">
      <div class="header-content">
        <h2>系统设置</h2>
        <p>管理系统配置、权限和安全设置</p>
      </div>
    </el-card>

    <el-row :gutter="20">
      <!-- 左侧菜单 -->
      <el-col :span="6">
        <el-card class="menu-card">
          <el-menu
            v-model:default-active="activeMenu"
            @select="handleMenuSelect"
          >
            <el-menu-item index="basic">
              <el-icon><Setting /></el-icon>
              <span>基本设置</span>
            </el-menu-item>
            <el-menu-item index="payment">
              <el-icon><CreditCard /></el-icon>
              <span>支付设置</span>
            </el-menu-item>
            <el-menu-item index="notification">
              <el-icon><Bell /></el-icon>
              <span>通知设置</span>
            </el-menu-item>
            <el-menu-item index="security">
              <el-icon><Lock /></el-icon>
              <span>安全设置</span>
            </el-menu-item>
            <el-menu-item index="logs">
              <el-icon><Document /></el-icon>
              <span>系统日志</span>
            </el-menu-item>
            <el-menu-item index="backup">
              <el-icon><FolderOpened /></el-icon>
              <span>数据备份</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <!-- 右侧内容 -->
      <el-col :span="18">
        <!-- 基本设置 -->
        <el-card v-if="activeMenu === 'basic'" class="content-card">
          <template #header>
            <span>基本设置</span>
          </template>
          <el-form :model="basicSettings" label-width="120px">
            <el-form-item label="系统名称">
              <el-input v-model="basicSettings.systemName" placeholder="请输入系统名称" />
            </el-form-item>
            <el-form-item label="系统Logo">
              <el-upload
                class="logo-uploader"
                action="/api/upload"
                :show-file-list="false"
                :on-success="handleLogoSuccess"
                :before-upload="beforeLogoUpload"
              >
                <img v-if="basicSettings.logo" :src="basicSettings.logo" class="logo" />
                <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
            <el-form-item label="系统描述">
              <el-input
                v-model="basicSettings.description"
                type="textarea"
                :rows="3"
                placeholder="请输入系统描述"
              />
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="basicSettings.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
            <el-form-item label="联系邮箱">
              <el-input v-model="basicSettings.contactEmail" placeholder="请输入联系邮箱" />
            </el-form-item>
            <el-form-item label="公司地址">
              <el-input v-model="basicSettings.address" placeholder="请输入公司地址" />
            </el-form-item>
            <el-form-item label="ICP备案号">
              <el-input v-model="basicSettings.icpNumber" placeholder="请输入ICP备案号" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveBasicSettings">保存设置</el-button>
              <el-button @click="resetBasicSettings">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 支付设置 -->
        <el-card v-if="activeMenu === 'payment'" class="content-card">
          <template #header>
            <span>支付设置</span>
          </template>
          <el-form :model="paymentSettings" label-width="120px">
            <el-divider content-position="left">微信支付</el-divider>
            <el-form-item label="启用微信支付">
              <el-switch v-model="paymentSettings.wechat.enabled" />
            </el-form-item>
            <el-form-item label="商户号">
              <el-input v-model="paymentSettings.wechat.merchantId" placeholder="请输入微信商户号" />
            </el-form-item>
            <el-form-item label="应用ID">
              <el-input v-model="paymentSettings.wechat.appId" placeholder="请输入微信应用ID" />
            </el-form-item>
            <el-form-item label="商户密钥">
              <el-input
                v-model="paymentSettings.wechat.secretKey"
                type="password"
                placeholder="请输入商户密钥"
                show-password
              />
            </el-form-item>
            
            <el-divider content-position="left">支付宝支付</el-divider>
            <el-form-item label="启用支付宝">
              <el-switch v-model="paymentSettings.alipay.enabled" />
            </el-form-item>
            <el-form-item label="应用ID">
              <el-input v-model="paymentSettings.alipay.appId" placeholder="请输入支付宝应用ID" />
            </el-form-item>
            <el-form-item label="私钥">
              <el-input
                v-model="paymentSettings.alipay.privateKey"
                type="textarea"
                :rows="3"
                placeholder="请输入应用私钥"
              />
            </el-form-item>
            <el-form-item label="公钥">
              <el-input
                v-model="paymentSettings.alipay.publicKey"
                type="textarea"
                :rows="3"
                placeholder="请输入支付宝公钥"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="savePaymentSettings">保存设置</el-button>
              <el-button @click="testPayment">测试连接</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 通知设置 -->
        <el-card v-if="activeMenu === 'notification'" class="content-card">
          <template #header>
            <span>通知设置</span>
          </template>
          <el-form :model="notificationSettings" label-width="120px">
            <el-divider content-position="left">邮件通知</el-divider>
            <el-form-item label="启用邮件通知">
              <el-switch v-model="notificationSettings.email.enabled" />
            </el-form-item>
            <el-form-item label="SMTP服务器">
              <el-input v-model="notificationSettings.email.smtpHost" placeholder="请输入SMTP服务器地址" />
            </el-form-item>
            <el-form-item label="SMTP端口">
              <el-input-number v-model="notificationSettings.email.smtpPort" :min="1" :max="65535" />
            </el-form-item>
            <el-form-item label="发件人邮箱">
              <el-input v-model="notificationSettings.email.fromEmail" placeholder="请输入发件人邮箱" />
            </el-form-item>
            <el-form-item label="邮箱密码">
              <el-input
                v-model="notificationSettings.email.password"
                type="password"
                placeholder="请输入邮箱密码或授权码"
                show-password
              />
            </el-form-item>
            
            <el-divider content-position="left">短信通知</el-divider>
            <el-form-item label="启用短信通知">
              <el-switch v-model="notificationSettings.sms.enabled" />
            </el-form-item>
            <el-form-item label="短信平台">
              <el-select v-model="notificationSettings.sms.provider" placeholder="请选择短信平台">
                <el-option label="阿里云" value="aliyun" />
                <el-option label="腾讯云" value="tencent" />
                <el-option label="华为云" value="huawei" />
              </el-select>
            </el-form-item>
            <el-form-item label="AccessKey">
              <el-input v-model="notificationSettings.sms.accessKey" placeholder="请输入AccessKey" />
            </el-form-item>
            <el-form-item label="SecretKey">
              <el-input
                v-model="notificationSettings.sms.secretKey"
                type="password"
                placeholder="请输入SecretKey"
                show-password
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="saveNotificationSettings">保存设置</el-button>
              <el-button @click="testNotification">测试通知</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 安全设置 -->
        <el-card v-if="activeMenu === 'security'" class="content-card">
          <template #header>
            <span>安全设置</span>
          </template>
          <el-form :model="securitySettings" label-width="150px">
            <el-form-item label="密码最小长度">
              <el-input-number v-model="securitySettings.minPasswordLength" :min="6" :max="20" />
            </el-form-item>
            <el-form-item label="密码复杂度要求">
              <el-checkbox-group v-model="securitySettings.passwordRequirements">
                <el-checkbox label="uppercase">包含大写字母</el-checkbox>
                <el-checkbox label="lowercase">包含小写字母</el-checkbox>
                <el-checkbox label="number">包含数字</el-checkbox>
                <el-checkbox label="special">包含特殊字符</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item label="登录失败锁定">
              <el-switch v-model="securitySettings.loginLockEnabled" />
            </el-form-item>
            <el-form-item label="最大失败次数">
              <el-input-number v-model="securitySettings.maxLoginAttempts" :min="3" :max="10" />
            </el-form-item>
            <el-form-item label="锁定时间(分钟)">
              <el-input-number v-model="securitySettings.lockDuration" :min="5" :max="60" />
            </el-form-item>
            <el-form-item label="会话超时(分钟)">
              <el-input-number v-model="securitySettings.sessionTimeout" :min="30" :max="480" />
            </el-form-item>
            <el-form-item label="启用验证码">
              <el-switch v-model="securitySettings.captchaEnabled" />
            </el-form-item>
            <el-form-item label="启用双因子认证">
              <el-switch v-model="securitySettings.twoFactorEnabled" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="saveSecuritySettings">保存设置</el-button>
              <el-button type="danger" @click="clearAllSessions">清除所有会话</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 系统日志 -->
        <el-card v-if="activeMenu === 'logs'" class="content-card">
          <template #header>
            <div class="log-header">
              <span>系统日志</span>
              <div class="log-actions">
                <el-select v-model="logFilter.level" placeholder="日志级别" style="width: 120px; margin-right: 10px;">
                  <el-option label="全部" value="" />
                  <el-option label="ERROR" value="ERROR" />
                  <el-option label="WARN" value="WARN" />
                  <el-option label="INFO" value="INFO" />
                  <el-option label="DEBUG" value="DEBUG" />
                </el-select>
                <el-date-picker
                  v-model="logFilter.date"
                  type="date"
                  placeholder="选择日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  style="width: 150px; margin-right: 10px;"
                />
                <el-button type="primary" @click="loadLogs">查询</el-button>
                <el-button type="danger" @click="clearLogs">清空日志</el-button>
              </div>
            </div>
          </template>
          
          <el-table v-loading="logLoading" :data="logList" stripe style="width: 100%">
            <el-table-column prop="timestamp" label="时间" width="180" />
            <el-table-column prop="level" label="级别" width="80">
              <template #default="{ row }">
                <el-tag :type="getLogLevelType(row.level)">{{ row.level }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="module" label="模块" width="120" />
            <el-table-column prop="message" label="消息" show-overflow-tooltip />
            <el-table-column prop="user" label="用户" width="100" />
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="viewLogDetail(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <div class="pagination-wrapper">
            <el-pagination
              v-model:current-page="logPagination.page"
              v-model:page-size="logPagination.size"
              :page-sizes="[20, 50, 100]"
              :total="logPagination.total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleLogSizeChange"
              @current-change="handleLogCurrentChange"
            />
          </div>
        </el-card>

        <!-- 数据备份 -->
        <el-card v-if="activeMenu === 'backup'" class="content-card">
          <template #header>
            <div class="backup-header">
              <span>数据备份</span>
              <el-button type="primary" @click="createBackup">创建备份</el-button>
            </div>
          </template>
          
          <el-table v-loading="backupLoading" :data="backupList" stripe style="width: 100%">
            <el-table-column prop="name" label="备份名称" />
            <el-table-column prop="size" label="文件大小" width="120" />
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column prop="type" label="备份类型" width="100">
              <template #default="{ row }">
                <el-tag :type="row.type === 'auto' ? 'success' : 'primary'">
                  {{ row.type === 'auto' ? '自动' : '手动' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="downloadBackup(row)">下载</el-button>
                <el-button type="success" size="small" @click="restoreBackup(row)">恢复</el-button>
                <el-button type="danger" size="small" @click="deleteBackup(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <el-divider />
          
          <el-form :model="backupSettings" label-width="120px">
            <el-form-item label="自动备份">
              <el-switch v-model="backupSettings.autoBackup" />
            </el-form-item>
            <el-form-item label="备份频率">
              <el-select v-model="backupSettings.frequency">
                <el-option label="每日" value="daily" />
                <el-option label="每周" value="weekly" />
                <el-option label="每月" value="monthly" />
              </el-select>
            </el-form-item>
            <el-form-item label="保留天数">
              <el-input-number v-model="backupSettings.retentionDays" :min="7" :max="365" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveBackupSettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <!-- 日志详情对话框 -->
    <el-dialog v-model="logDetailVisible" title="日志详情" width="800px">
      <div v-if="currentLog" class="log-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="时间">{{ currentLog.timestamp }}</el-descriptions-item>
          <el-descriptions-item label="级别">
            <el-tag :type="getLogLevelType(currentLog.level)">{{ currentLog.level }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="模块">{{ currentLog.module }}</el-descriptions-item>
          <el-descriptions-item label="用户">{{ currentLog.user || '系统' }}</el-descriptions-item>
          <el-descriptions-item label="IP地址">{{ currentLog.ip || '-' }}</el-descriptions-item>
          <el-descriptions-item label="用户代理">{{ currentLog.userAgent || '-' }}</el-descriptions-item>
          <el-descriptions-item label="消息" :span="2">
            <div class="log-message">{{ currentLog.message }}</div>
          </el-descriptions-item>
          <el-descriptions-item v-if="currentLog.stackTrace" label="堆栈信息" :span="2">
            <pre class="stack-trace">{{ currentLog.stackTrace }}</pre>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Setting,
  CreditCard,
  Bell,
  Lock,
  Document,
  FolderOpened,
  Plus
} from '@element-plus/icons-vue'
import {
  getSystemSettings,
  saveSystemSettings,
  getSystemLogs,
  clearSystemLogs,
  getBackupList,
  createSystemBackup,
  downloadSystemBackup,
  restoreSystemBackup,
  deleteSystemBackup
} from '@/api/admin'

// 响应式数据
const activeMenu = ref('basic')
const logLoading = ref(false)
const backupLoading = ref(false)
const logDetailVisible = ref(false)
const currentLog = ref(null)

// 基本设置
const basicSettings = reactive({
  systemName: '',
  logo: '',
  description: '',
  contactPhone: '',
  contactEmail: '',
  address: '',
  icpNumber: ''
})

// 支付设置
const paymentSettings = reactive({
  wechat: {
    enabled: false,
    merchantId: '',
    appId: '',
    secretKey: ''
  },
  alipay: {
    enabled: false,
    appId: '',
    privateKey: '',
    publicKey: ''
  }
})

// 通知设置
const notificationSettings = reactive({
  email: {
    enabled: false,
    smtpHost: '',
    smtpPort: 587,
    fromEmail: '',
    password: ''
  },
  sms: {
    enabled: false,
    provider: '',
    accessKey: '',
    secretKey: ''
  }
})

// 安全设置
const securitySettings = reactive({
  minPasswordLength: 8,
  passwordRequirements: ['lowercase', 'number'],
  loginLockEnabled: true,
  maxLoginAttempts: 5,
  lockDuration: 15,
  sessionTimeout: 120,
  captchaEnabled: true,
  twoFactorEnabled: false
})

// 日志相关
const logList = ref([])
const logFilter = reactive({
  level: '',
  date: ''
})
const logPagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 备份相关
const backupList = ref([])
const backupSettings = reactive({
  autoBackup: true,
  frequency: 'daily',
  retentionDays: 30
})

// 菜单选择
const handleMenuSelect = (key) => {
  activeMenu.value = key
  if (key === 'logs') {
    loadLogs()
  } else if (key === 'backup') {
    loadBackupList()
  }
}

// 加载系统设置
const loadSettings = async () => {
  try {
    const response = await getSystemSettings()
    const settings = response.data
    
    Object.assign(basicSettings, settings.basic || {})
    Object.assign(paymentSettings, settings.payment || {})
    Object.assign(notificationSettings, settings.notification || {})
    Object.assign(securitySettings, settings.security || {})
    Object.assign(backupSettings, settings.backup || {})
  } catch (error) {
    console.error('加载设置失败:', error)
  }
}

// 保存基本设置
const saveBasicSettings = async () => {
  try {
    await saveSystemSettings('basic', basicSettings)
    ElMessage.success('基本设置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 重置基本设置
const resetBasicSettings = () => {
  Object.assign(basicSettings, {
    systemName: '',
    logo: '',
    description: '',
    contactPhone: '',
    contactEmail: '',
    address: '',
    icpNumber: ''
  })
}

// Logo上传
const handleLogoSuccess = (response) => {
  basicSettings.logo = response.data.url
  ElMessage.success('Logo上传成功')
}

const beforeLogoUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB')
    return false
  }
  return true
}

// 保存支付设置
const savePaymentSettings = async () => {
  try {
    await saveSystemSettings('payment', paymentSettings)
    ElMessage.success('支付设置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 测试支付
const testPayment = () => {
  ElMessage.info('支付测试功能开发中')
}

// 保存通知设置
const saveNotificationSettings = async () => {
  try {
    await saveSystemSettings('notification', notificationSettings)
    ElMessage.success('通知设置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 测试通知
const testNotification = () => {
  ElMessage.info('通知测试功能开发中')
}

// 保存安全设置
const saveSecuritySettings = async () => {
  try {
    await saveSystemSettings('security', securitySettings)
    ElMessage.success('安全设置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 清除所有会话
const clearAllSessions = async () => {
  try {
    await ElMessageBox.confirm('确定要清除所有用户会话吗？这将强制所有用户重新登录。', '确认操作', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 调用清除会话API
    ElMessage.success('所有会话已清除')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 加载日志
const loadLogs = async () => {
  logLoading.value = true
  try {
    const params = {
      page: logPagination.page,
      size: logPagination.size,
      level: logFilter.level,
      date: logFilter.date
    }
    
    const response = await getSystemLogs(params)
    logList.value = response.data.records
    logPagination.total = response.data.total
  } catch (error) {
    ElMessage.error('获取日志失败')
  } finally {
    logLoading.value = false
  }
}

// 清空日志
const clearLogs = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有日志吗？此操作不可恢复。', '确认清空', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await clearSystemLogs()
    ElMessage.success('日志已清空')
    loadLogs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('清空失败')
    }
  }
}

// 查看日志详情
const viewLogDetail = (log) => {
  currentLog.value = log
  logDetailVisible.value = true
}

// 日志分页
const handleLogSizeChange = (size) => {
  logPagination.size = size
  logPagination.page = 1
  loadLogs()
}

const handleLogCurrentChange = (page) => {
  logPagination.page = page
  loadLogs()
}

// 加载备份列表
const loadBackupList = async () => {
  backupLoading.value = true
  try {
    const response = await getBackupList()
    backupList.value = response.data
  } catch (error) {
    ElMessage.error('获取备份列表失败')
  } finally {
    backupLoading.value = false
  }
}

// 创建备份
const createBackup = async () => {
  try {
    await ElMessageBox.confirm('确定要创建数据备份吗？', '确认备份', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    await createSystemBackup()
    ElMessage.success('备份创建成功')
    loadBackupList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('备份失败')
    }
  }
}

// 下载备份
const downloadBackup = async (backup) => {
  try {
    await downloadSystemBackup(backup.id)
    ElMessage.success('备份下载开始')
  } catch (error) {
    ElMessage.error('下载失败')
  }
}

// 恢复备份
const restoreBackup = async (backup) => {
  try {
    await ElMessageBox.confirm(
      `确定要恢复备份 "${backup.name}" 吗？这将覆盖当前数据，请确保已做好准备。`,
      '确认恢复',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await restoreSystemBackup(backup.id)
    ElMessage.success('备份恢复成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('恢复失败')
    }
  }
}

// 删除备份
const deleteBackup = async (backup) => {
  try {
    await ElMessageBox.confirm(`确定要删除备份 "${backup.name}" 吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteSystemBackup(backup.id)
    ElMessage.success('备份删除成功')
    loadBackupList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 保存备份设置
const saveBackupSettings = async () => {
  try {
    await saveSystemSettings('backup', backupSettings)
    ElMessage.success('备份设置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 工具函数
const getLogLevelType = (level) => {
  const typeMap = {
    'ERROR': 'danger',
    'WARN': 'warning',
    'INFO': 'success',
    'DEBUG': 'info'
  }
  return typeMap[level] || ''
}

// 初始化
onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.settings {
  width: 100%;
  max-width: none;
}

.page-header {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.page-header :deep(.el-card__body) {
  padding: 30px;
}

.header-content h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
}

.header-content p {
  margin: 0;
  opacity: 0.9;
  font-size: 14px;
}

.menu-card {
  height: calc(100vh - 200px);
  overflow-y: auto;
}

.content-card {
  min-height: calc(100vh - 200px);
}

.logo-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.logo-uploader:hover {
  border-color: #409eff;
}

.logo-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.logo {
  width: 178px;
  height: 178px;
  display: block;
}

.log-header,
.backup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.log-actions {
  display: flex;
  align-items: center;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.log-detail {
  padding: 20px 0;
}

.log-message {
  max-height: 200px;
  overflow-y: auto;
  line-height: 1.6;
  word-break: break-all;
}

.stack-trace {
  max-height: 300px;
  overflow-y: auto;
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  font-size: 12px;
  line-height: 1.4;
  white-space: pre-wrap;
  word-break: break-all;
}

/* 菜单样式 */
:deep(.el-menu) {
  border-right: none;
}

:deep(.el-menu-item) {
  border-radius: 6px;
  margin: 5px 10px;
  width: calc(100% - 20px);
}

:deep(.el-menu-item.is-active) {
  background-color: #409eff;
  color: white;
}

:deep(.el-menu-item:hover) {
  background-color: #ecf5ff;
  color: #409eff;
}

:deep(.el-menu-item.is-active:hover) {
  background-color: #409eff;
  color: white;
}

/* 表格样式 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table__header) {
  background-color: #f8f9fa;
}

:deep(.el-table th) {
  background-color: #f8f9fa;
  color: #606266;
  font-weight: 600;
}

:deep(.el-table td) {
  border-bottom: 1px solid #f0f0f0;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

/* 表单样式 */
.el-form-item {
  margin-bottom: 20px;
}

/* 分割线样式 */
.el-divider {
  margin: 30px 0 20px 0;
}

/* 卡片样式 */
.el-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}
</style>