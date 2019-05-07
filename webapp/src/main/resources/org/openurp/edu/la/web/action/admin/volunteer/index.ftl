[#ftl]
[@b.head/]
[@b.toolbar title="法律助理报名名单"/]
<table class="indexpanel">
  <tr>
    <td class="index_view" >
    [@b.form name="laTakerSearchForm" action="!search" target="laTakerlist" title="ui.searchForm" theme="search"]
      [@b.textfield name="laTaker.rank" label="志愿顺序"/]
      [@b.textfield name="laTaker.volunteer.std.user.code" label="学号"/]
      [@b.textfield name="laTaker.volunteer.std.user.name" label="姓名"/]
      [@b.textfield name="laTaker.option.corporation.name" label="单位名称"/]
      [@b.select label="学年学期" name="laTaker.option.semester.id" items={}]
        <option value="">...</option>
        [#list semesters?sort_by("code")?reverse as semester]
          <option value="${semester.id}" [#if semester.id = currentSemester.id]selected[/#if]>${(semester.schoolYear)!}学年${(semester.name?replace('0','第'))!}学期</option>
        [/#list]
      [/@]
      [@b.radios name="laTaker.volunteer.adjustable" label="是否服从调剂" items="1:是,0:否" value=""/]
      <input type="hidden" name="orderBy" value="laTaker.volunteer.std.user.code"/>
    [/@]
    </td>
    <td class="index_content">[@b.div id="laTakerlist" href="!search?orderBy=laTaker.updatedAt desc"/]
    </td>
  </tr>
</table>
[@b.foot/]
