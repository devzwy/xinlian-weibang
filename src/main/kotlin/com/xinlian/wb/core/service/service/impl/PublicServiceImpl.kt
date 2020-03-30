package com.xinlian.wb.core.service.service.impl

import com.xinlian.wb.core.entity.HttpResponse
import com.xinlian.wb.core.entity.ReqBeanGetWithdrawal
import com.xinlian.wb.core.entity.ReqBeanWithdrawal
import com.xinlian.wb.core.service.service.IPublicService
import com.xinlian.wb.jdbc.repositorys.BalanceNotesRepository
import com.xinlian.wb.jdbc.repositorys.BalanceRepository
import com.xinlian.wb.jdbc.repositorys.UserRepository
import com.xinlian.wb.jdbc.repositorys_web.User_WebRepository
import com.xinlian.wb.jdbc.repositorys_web.WebBalanceNoteRepository
import com.xinlian.wb.jdbc.tabs.BalanceNotes
import com.xinlian.wb.jdbc.tabs.WithDrawal
import com.xinlian.wb.jdbc.tabs.WithDrawalRepository
import com.xinlian.wb.jdbc.tabs_web.WebBalanceNoteTab
import com.xinlian.wb.util.Helper
import com.xinlian.wb.util.ktx.getErrorRespons
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service


@Service
@Configuration
open class PublicServiceImpl : IPublicService {

    @Autowired
    lateinit var mUserRepository: UserRepository

    @Autowired
    lateinit var mUser_WebRepository: User_WebRepository

    @Autowired
    lateinit var mBalanceRepository: BalanceRepository

    @Autowired
    lateinit var mWithDrawalRepository: WithDrawalRepository

    /**
     * 余额流水表
     */
    @Autowired
    lateinit var mBalanceNotesRepository: BalanceNotesRepository

    @Autowired
    lateinit var mWebBalanceNoteRepository: WebBalanceNoteRepository


    /**
     * 提现申请
     */
    override fun withdrawal(mReqBeanWithdrawal: ReqBeanWithdrawal, token: String): HttpResponse<Any> {
        val uid = Helper.getUserIdByToken(token)
        //区分代理商还是用户
        val user = mUserRepository.findTop1ByUserId(uid)
        val user_web = mUser_WebRepository.findTop1ByUserId(uid)

        if (user != null) {
            //用户提现
            if ((mBalanceRepository.findTop1ByUserId(uid)?.balance
                            ?: 0.0) < mReqBeanWithdrawal.price) return "余额不足本次提现".getErrorRespons()

            //记录提现流水
            val a = mWithDrawalRepository.saveAndFlush(WithDrawal(
                    userType = 0,
                    state = 0,
                    price = mReqBeanWithdrawal.price,
                    alipayUserName = mReqBeanWithdrawal.alipayUserName,
                    aliaAccount = mReqBeanWithdrawal.aliaAccount,
                    userId = uid
            ))

            //记录余额变动流水
            mBalanceNotesRepository.saveAndFlush(BalanceNotes(
                    userId = uid,
                    bType = 3,
                    orderId = a.id.toString(),
                    orderDec = "余额提现",
                    price = mReqBeanWithdrawal.price.toFloat(),
                    payType = 2
            ))
            //记录余额变动
            val b = mBalanceRepository.findTop1ByUserId(uid)
            b?.balance = (b?.balance ?: 0.0) - mReqBeanWithdrawal.price
            mBalanceRepository.saveAndFlush(b)
            return HttpResponse("提交成功")
        } else if (user_web != null) {
            if (user_web.userId.equals("admin")) return "管理员不能提现".getErrorRespons()
            //代理商提现
            if (user_web.balance ?: 0.0f < mReqBeanWithdrawal.price) return "余额不足本次提现".getErrorRespons()
            //记录提现流水
            val a = mWithDrawalRepository.saveAndFlush(WithDrawal(
                    userType = 1,
                    state = 0,
                    price = mReqBeanWithdrawal.price,
                    alipayUserName = mReqBeanWithdrawal.alipayUserName,
                    aliaAccount = mReqBeanWithdrawal.aliaAccount,
                    userId = uid
            ))
            //记录余额变动流水
            mWebBalanceNoteRepository.saveAndFlush(WebBalanceNoteTab(userId = uid, bType = 2, price = mReqBeanWithdrawal.price.toFloat(),
                    toOrFromUserId = uid,
                    noteDec = "提现", bFType = 1))
            //记录余额变动
            user_web.balance = (user_web.balance ?: 0.0F) - mReqBeanWithdrawal.price.toFloat()
            mUser_WebRepository.saveAndFlush(user_web)
            return HttpResponse("提交成功")
        } else {
            return "用户不存在".getErrorRespons()
        }
    }

    /**
     * 获取提现申请记录 用户、代理商、管理员
     */
    override fun getWithdrawal(mReqBeanGetWithdrawal: ReqBeanGetWithdrawal, token: String): HttpResponse<Any> {
        if (mReqBeanGetWithdrawal.page <= 0) {
            return "请求的页数不能为0".getErrorRespons()
        }
        val uid = Helper.getUserIdByToken(token)
        //区分代理商还是用户或者管理员
        val user = mUserRepository.findTop1ByUserId(uid)
        val user_web = mUser_WebRepository.findTop1ByUserId(uid)

        var list_resp = listOf<WithDrawal>()
        if (user != null) {
            //用户自己获取自己的提现记录
            if (mReqBeanGetWithdrawal.state == -1) {
                //查询用户自己的全部提现流水
                list_resp = mWithDrawalRepository.findAll(user_id = uid, page = mReqBeanGetWithdrawal.page - 1, number = mReqBeanGetWithdrawal.number)
            } else {
                //查询用户自己的提现流水并用state筛选
                list_resp = mWithDrawalRepository.findAll(user_id = uid, state = mReqBeanGetWithdrawal.state, page = mReqBeanGetWithdrawal.page - 1, number = mReqBeanGetWithdrawal.number)
            }
        } else if (user_web != null) {
            //web端用户获取提现记录 区分代理商和管理员
            if (user_web.userId.equals("admin")) {
                //管理员
                if (mReqBeanGetWithdrawal.bType == -1) {
                    //查询全部提现流水
                    if (mReqBeanGetWithdrawal.state == -1) {
                        //查询全部提现流水
                        list_resp = mWithDrawalRepository.findAll(page = mReqBeanGetWithdrawal.page - 1, number = mReqBeanGetWithdrawal.number)
                    } else {
                        //查询全部提现流水并用state筛选
                        list_resp = mWithDrawalRepository.findAll(state = mReqBeanGetWithdrawal.state, page = mReqBeanGetWithdrawal.page - 1, number = mReqBeanGetWithdrawal.number)
                    }
                } else if (mReqBeanGetWithdrawal.bType == 0) {
                    //查询用户提现流水
                    if (mReqBeanGetWithdrawal.state == -1) {
                        //查询用户全部提现流水
                        list_resp = mWithDrawalRepository.findAllByUserType(page = mReqBeanGetWithdrawal.page - 1, number = mReqBeanGetWithdrawal.number)
                    } else {
                        //查询用户全部提现流水并用state筛选
                        list_resp = mWithDrawalRepository.findAllByUserType(state = mReqBeanGetWithdrawal.state, page = mReqBeanGetWithdrawal.page - 1, number = mReqBeanGetWithdrawal.number)
                    }
                } else if (mReqBeanGetWithdrawal.bType == 1) {
                    //查询代理商提现流水
                    if (mReqBeanGetWithdrawal.state == -1) {
                        //查询代理商全部提现流水
                        list_resp = mWithDrawalRepository.findAllByUserType2(page = mReqBeanGetWithdrawal.page - 1, number = mReqBeanGetWithdrawal.number)
                    } else {
                        //查询代理商提现流水并用state筛选
                        list_resp = mWithDrawalRepository.findAllByUserType2(state = mReqBeanGetWithdrawal.state, page = mReqBeanGetWithdrawal.page - 1, number = mReqBeanGetWithdrawal.number)
                    }
                } else {
                    return "参数错误".getErrorRespons()
                }
            } else {
                //代理商 查询自己的流水
                if (mReqBeanGetWithdrawal.state == -1) {
                    //查询代理商自己的全部提现流水
                    list_resp = mWithDrawalRepository.findAll(user_id = uid, page = mReqBeanGetWithdrawal.page - 1, number = mReqBeanGetWithdrawal.number)
                } else {
                    //查询代理商自己的提现流水并用state筛选
                    list_resp = mWithDrawalRepository.findAll(user_id = uid, state = mReqBeanGetWithdrawal.state, page = mReqBeanGetWithdrawal.page - 1, number = mReqBeanGetWithdrawal.number)
                }
            }
        } else {
            return "用户不存在".getErrorRespons()
        }

        return HttpResponse(list_resp.map {
            it.user = if (it.userType == 0) {
                mUserRepository.findTop1ByUserId(it.userId).also {
                    it?.address = null
                    it?.indentity = null
                    it?.password = null
                }
            } else {
                mUser_WebRepository.findTop1ByUserId(it.userId)
            }
            it
        })

//        val pageable = PageRequest.of(mReqBeanGetWithdrawal.page - 1, mReqBeanGetWithdrawal.number);
//        val list = mWithDrawalRepository.findAll(pageable).content
//        list.map {
//            it.user = mUserRepository.findTop1ByUserId(it.userId)?.apply {
//                indentity = null
//            }
//        }
//        if (mReqBeanGetWithdrawal.state == -1) {
//            //获取全部状态的记录
//            return HttpResponse(list)
//        } else {
//            //获取状态对应的记录
//            return HttpResponse(list.filter {
//                it.state == mReqBeanGetWithdrawal.state
//            })
//        }
    }
}
