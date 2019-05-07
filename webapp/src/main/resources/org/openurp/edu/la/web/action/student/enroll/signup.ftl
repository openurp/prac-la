[#ftl]
[@b.head/]
[@b.toolbar title="填写报名信息"]bar.addBack();[/@]
[@b.form action="!choose" theme="list"]
  [@b.field label="学号"]${student.user.code}[/@]
  [@b.field label="姓名"]${student.user.name}[/@]
  [@b.field label="院系"]${(student.state.department.name)!'--'}[/@]
  [@b.textfield label="绩点" name="volunteer.gpa" id="gpa" readonly="readonly" value=volunteer.gpa style="width:50px"/]
  [@b.textfield label="联系方式" name="volunteer.mobile" value=volunteer.mobile! style="width:100px" required="true"/]
  [@b.radios label="是否接受调剂" name="volunteer.adjustable" items={'1':'接受','0':'不接受'} value=volunteer.adjustable! required="true"/]
  [@b.select label="第一志愿" name="option1" items=options value=(volunteer.getTaker(1).option)! option=r"${item.corporation.name}" required="true" style="width:150px" empty="..."/]
  [@b.select label="第二志愿" name="option2" items=options value=(volunteer.getTaker(2).option)! option=r"${item.corporation.name}"  required="false" style="width:150px" empty="..."/]
  [@b.formfoot]
    [#if volunteer.persisted]
    <input type="hidden" name="volunteer.id" value="${volunteer.id}"/>
    [/#if]
    <input type="hidden" name="session.id" value="${session.id}"/>
    [@b.submit value="提交"/]
  [/@]
[/@]

  [#if options?? && options?size>0]
    [@b.grid items=options var="option" sortable="false"]
      [@b.row]
        [@b.col width="8%" title="序号"]${option_index+1 }[/@]
        [@b.col width="30%" property="corporation.name" title="单位名称"/]
        [@b.col width="38%" property="requirement" title="要求"/]
        [@b.col width="8%" property="capacity" title="人数上限"/]
        [@b.col width="8%" property="enrollLimit" title="拟录取"/]
        [@b.col width="8%" property="actual" title="实际报名"/]
      [/@]
    [/@]
  [#else]目前没有可报名单位[/#if]
[@b.foot/]
