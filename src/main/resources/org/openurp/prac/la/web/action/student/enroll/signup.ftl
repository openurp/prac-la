[#ftl]
[@b.head/]
[@b.toolbar title="填写报名信息"]bar.addBack();[/@]
<div class="container-fluid">

[#assign optionNames=['第一志愿','第二志愿','第三志愿','第四志愿','第五志愿']]
[@b.form action="!choose" theme="list"]
  [@b.field label="学号"]${student.code}[/@]
  [@b.field label="姓名"]${student.name}[/@]
  [@b.field label="院系"]${(student.state.department.name)!'--'}[/@]
  [@b.textfield label="绩点" name="volunteer.gpa" id="gpa" readonly="readonly" value=volunteer.gpa style="width:50px"/]
  [@b.textfield label="联系方式" name="volunteer.mobile" value=volunteer.mobile! style="width:100px" required="true"/]
  [#list 1..session.optionCount as i]
    [#assign required][#if i==1]true[#else]false[/#if][/#assign]
    [@b.select label=optionNames[i-1] name="option${i}" items=options value=(volunteer.getTaker(i?int).option)! option=r"${item.corporation.name}" required=required style="width:150px" empty="..."/]
  [/#list]
  [#if session.attachmentRequired]
    [@b.file name="attachment" label="个人简历" required="true"/]
  [/#if]
  [@b.formfoot]
    [#if volunteer.persisted]
    <input type="hidden" name="volunteer.id" value="${volunteer.id}"/>
    [/#if]
    <input type="hidden" name="session.id" value="${session.id}"/>
    [@b.submit value="提交"/]
  [/@]
[/@]

  [#if options?? && options?size>0]
    <div style="border:0.5px solid #006CB2;">
    [@b.grid items=options var="option" sortable="false"]
      [@b.row]
        [@b.col width="8%" title="序号"]${option_index+1 }[/@]
        [@b.col width="30%" property="corporation.name" title="单位名称"/]
        [@b.col width="38%" property="requirement" title="要求"/]
        [@b.col width="8%" property="capacity" title="最大面试人数"/]
        [@b.col width="8%" property="enrollLimit" title="拟录取人数"/]
        [@b.col width="8%" title="报名人数"]${option.takers?size}[/@]
      [/@]
    [/@]
   </div>
  [#else]目前没有可报名单位[/#if]

</div>
[@b.foot/]
