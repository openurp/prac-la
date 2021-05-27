[#list volunteers as volunteer]
[@b.form action="!index" theme="list"]
  [@b.field label="学号"]${volunteer.std.user.code}[/@]
  [@b.field label="姓名"]${volunteer.std.user.name}[/@]
  [@b.field label="院系"]${(volunteer.std.state.department.name)!'--'}[/@]
  [@b.field label="绩点"]${volunteer.gpa!}[/@]
  [@b.field label="联系方式"]${volunteer.mobile}[/@]
  [@b.field label="第一志愿"]
   ${(volunteer.getTaker(1).option.corporation.name)!}
     [#if ((volunteer.getTaker(1).option.id)!0)=((volunteer.enrolledOption.id)!-1)] <span class="glyphicon glyphicon-ok">👍🎉</span>[/#if]

  [/@]
  [@b.field label="第二志愿"]${(volunteer.getTaker(2).option.corporation.name)!"---"}
   [#if ((volunteer.getTaker(2).option.id)!0)=((volunteer.enrolledOption.id)!-1)] <span class="glyphicon glyphicon-ok"></span>[/#if]
  [/@]
  [@b.field label="报名时间"]${volunteer.updatedAt?string("yyyy-MM-dd HH:mm:ss")}[/@]
[/@]
[/#list]
