[#ftl]
[@b.head/]
[@b.toolbar title="${option.corporation.name }的面试名单"]bar.addBack();[/@]
[@b.grid items=option.volunteers var="volunteer"]
  [@b.row]
      [@b.col property="enrolledRank" title="志愿" width="5%"/]
      [@b.col property="std.user.code" title="学号" width="10%"/]
      [@b.col property="std.user.name" title="姓名" width="9%"/]
      [@b.col property="std.state.grade" title="年级" width="8%"/]
      [@b.col property="std.state.department.name" title="院系" width="20%"/]
      [@b.col property="std.state.major.name" title="专业" width="20%"]${(volunteer.std.state.major.name)!}[/@]
      [@b.col property="gpa"  title="平均绩点" width="10%"/]
      [@b.col property="updatedAt"  title="报名时间" width="18%"]${volunteer.updatedAt?string('MM-dd HH:mm:ss')}[/@]
  [/@]
[/@]
[@b.foot/]
