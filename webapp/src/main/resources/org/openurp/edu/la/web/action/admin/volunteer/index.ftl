[#ftl]
[@b.head/]
[@b.toolbar title="报名法律助理名单"/]
<table class="indexpanel">
  <tr>
    <td class="index_view" >
    [@b.form name="volunteerSearchForm" action="!search" target="volunteerlist" title="ui.searchForm" theme="search"]
      [@b.textfield name="volunteer.indexno" label="志愿顺序"/]
      [@b.textfield name="volunteer.std.user.code" label="学号"/]
      [@b.textfield name="volunteer.std.user.name" label="姓名"/]
      [@b.textfield name="volunteer.option.corporation.name" label="单位名称"/]
      [@b.select label="学年学期" name="volunteer.option.semester.id" items={}]
        <option value="">...</option>
        [#list semesters?sort_by("code")?reverse as semester]
          <option value="${semester.id}" [#if semester.id = currentSemester.id]selected[/#if]>${(semester.schoolYear)!}学年${(semester.name?replace('0','第'))!}学期</option>
        [/#list]
      [/@]
      [@b.radios name="volunteer.adjust" label="是否服从调剂" items="1:是,0:否" value=""/]
      <input type="hidden" name="orderBy" value="std.user.code"/>
    [/@]
    </td>
    <td class="index_content">[@b.div id="volunteerlist" href="!search?orderBy=std.user.code"/]
    </td>
  </tr>
</table>
[@b.foot/]
